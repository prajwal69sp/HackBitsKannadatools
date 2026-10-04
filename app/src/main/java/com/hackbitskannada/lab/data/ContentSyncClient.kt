package com.hackbitskannada.lab.data

import androidx.room.withTransaction
import com.hackbitskannada.lab.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

internal class ContentSyncClient(
    private val database: AppDatabase,
    private val baseUrl: String = BuildConfig.CONTENT_API_BASE_URL,
) {
    suspend fun syncPublishedContent(): Int = withContext(Dispatchers.IO) {
        if (baseUrl.isBlank()) return@withContext 0
        val normalized = ContentApiAddress.normalize(baseUrl, BuildConfig.DEBUG)
            ?: throw IllegalArgumentException("CONTENT_API_BASE_URL must be a valid HTTPS URL.")
        val dao = database.contentDao()
        val categories = getResource("$normalized/api/v1/categories")
        var synced = 0
        dao.upsertSyncedCategories(categories.map { item ->
            CategoryEntity(
                name = item.optString("name"),
                description = item.optString("description"),
                section = item.optString("section", "General"),
            )
        })

        for (resource in CONTENT_RESOURCES) {
            var page = 1
            var pages: Int
            do {
                val url = "$normalized/api/v1/$resource?page=$page&pageSize=100"
                val response = getPage(url)
                database.withTransaction {
                    when (resource) {
                        "commands" -> dao.upsertSyncedCommands(response.items.map(::command))
                        "scripts" -> dao.upsertSyncedScripts(response.items.map(::script))
                        "tools" -> dao.upsertSyncedTools(response.items.map(::tool))
                        "tutorials" -> dao.upsertSyncedTutorials(response.items.flatMap(::tutorialLessons))
                    }
                }
                synced += response.items.size
                pages = response.pages
                page += 1
            } while (page <= pages)
        }
        synced
    }

    private fun getResource(url: String): List<JSONObject> =
        readJson(url).optJSONArray("data").toObjects()

    private fun getPage(url: String): Page {
        val body = readJson(url)
        val pagination = body.optJSONObject("pagination")
            ?: throw IllegalStateException("The content API response is missing pagination metadata.")
        return Page(
            items = body.optJSONArray("data").toObjects(),
            pages = pagination.optInt("pages", 1).coerceAtLeast(1),
        )
    }

    private fun readJson(address: String): JSONObject {
        val connection = URL(address).openConnection() as HttpURLConnection
        connection.connectTimeout = CONNECTION_TIMEOUT_MS
        connection.readTimeout = READ_TIMEOUT_MS
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")
        connection.instanceFollowRedirects = false
        try {
            val status = connection.responseCode
            if (status !in 200..299) {
                connection.errorStream?.close()
                throw IllegalStateException("Content API request failed with HTTP $status.")
            }
            val payload = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val body = JSONObject(payload)
            if (!body.optBoolean("success", false)) {
                throw IllegalStateException("Content API returned an unsuccessful response.")
            }
            return body
        } finally {
            connection.disconnect()
        }
    }

    private fun command(json: JSONObject) = CommandEntity(
        id = json.required("id"),
        title = json.required("title"),
        command = json.required("command"),
        category = json.optString("category"),
        description = json.optString("description"),
        syntax = json.optString("syntax"),
        example = json.optString("example"),
        expectedUsage = json.optString("expectedOutput"),
        difficulty = json.optString("difficulty", "BEGINNER"),
        tags = json.optJSONArray("tags").tagNames(),
        warning = json.optString("warning"),
        relatedCommands = json.optString("relatedCommands"),
        createdAt = json.optString("updatedAt").toEpochMillis(),
    )

    private fun script(json: JSONObject) = ScriptEntity(
        id = json.required("id"),
        title = json.required("title"),
        category = json.optString("category"),
        difficulty = json.optString("difficulty", "BEGINNER"),
        purpose = json.optString("description"),
        code = json.optString("code"),
        explanation = json.optString("explanation"),
        howToRun = json.optString("usage"),
        expectedOutput = json.optString("exampleOutput"),
        safetyNotes = json.optString("warning"),
    )

    private fun tool(json: JSONObject) = ToolEntity(
        id = json.required("id"),
        name = json.required("name"),
        category = json.optString("category"),
        description = json.optString("description"),
        purpose = json.optString("description"),
        installCommand = json.optString("installationCommand"),
        basicUsage = json.optString("basicUsage"),
        example = json.optString("examples"),
        commonErrors = "",
        troubleshooting = "",
        safetyNote = json.optString("warning"),
    )

    private fun tutorialLessons(json: JSONObject): List<TutorialEntity> {
        val id = json.required("id")
        val title = json.optString("title")
        val level = json.optString("difficulty", "BEGINNER")
        val tags = json.optJSONArray("tags").tagNames()
        val lessons = json.optJSONArray("lessons")
        if (lessons == null || lessons.length() == 0) {
            return listOf(
                TutorialEntity(
                    id = id,
                    title = title,
                    level = level,
                    section = json.optString("category"),
                    lessonOrder = 0,
                    body = json.optString("description"),
                    tags = tags,
                ),
            )
        }
        return lessons.toObjects().mapIndexed { index, lesson ->
            TutorialEntity(
                id = "$id:${lesson.required("id")}",
                title = lesson.optString("title"),
                level = level,
                section = title,
                lessonOrder = lesson.optInt("sortOrder", index),
                body = lesson.optString("content"),
                tags = tags,
            )
        }
    }

    private fun String.toEpochMillis(): Long = try {
        java.time.Instant.parse(this).toEpochMilli()
    } catch (_: Exception) {
        System.currentTimeMillis()
    }

    private fun JSONObject.required(key: String): String =
        optString(key).takeIf { it.isNotBlank() && it != "null" }
            ?: throw IllegalStateException("A content API record is missing $key.")

    private fun JSONArray?.toObjects(): List<JSONObject> {
        if (this == null) return emptyList()
        return List(length()) { index ->
            optJSONObject(index) ?: throw IllegalStateException("The content API returned an invalid record.")
        }
    }

    private fun JSONArray?.tagNames(): String {
        if (this == null) return ""
        return List(length()) { index ->
            val value = optJSONObject(index) ?: return@List optString(index)
            value.optJSONObject("tag")?.optString("name") ?: value.optString("name")
        }.filter { it.isNotBlank() }.joinToString(",")
    }

    private data class Page(val items: List<JSONObject>, val pages: Int)

    private companion object {
        const val CONNECTION_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 20_000
        val CONTENT_RESOURCES = listOf("commands", "scripts", "tools", "tutorials")
    }
}
