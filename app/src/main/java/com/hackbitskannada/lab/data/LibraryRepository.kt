package com.hackbitskannada.lab.data

import android.content.Context
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LibraryRepository(context: Context) {
    private val database = AppDatabase.get(context)
    private val contentDao = database.contentDao()
    private val userDao = database.userDataDao()

    val categories = contentDao.observeCategories()
    val commands = contentDao.observeCommands()
    val scripts = contentDao.observeScripts()
    val tools = contentDao.observeTools()
    val tutorials = contentDao.observeTutorials()
    val favorites = userDao.observeFavorites()
    val completedLessons = userDao.observeCompletedLessons()
    val recentlyViewed = contentDao.observeRecentlyViewed()
    val settings = userDao.observeSettings()

    suspend fun seedIfNeeded() = database.withTransaction {
        if (contentDao.commandCount() == 0) {
            contentDao.insertCategories(CommandSeed.categories)
            contentDao.insertCommands(CommandSeed.commands)
            contentDao.insertScripts(LearningSeed.scripts)
            contentDao.insertTools(LearningSeed.tools)
            contentDao.insertTutorials(LearningSeed.tutorials)
        }
        if (userDao.settingsOnce() == null) userDao.saveSettings(AppSettingsEntity())
    }

    suspend fun toggleFavorite(type: String, id: String) {
        if (userDao.observeFavorite(type, id).first()) {
            userDao.removeFavorite(type, id)
        } else {
            userDao.saveFavorite(FavoriteEntity(type, id))
        }
    }

    suspend fun setLessonCompleted(id: String, completed: Boolean) {
        userDao.setProgress(ProgressEntity("tutorial", id, completed))
    }

    suspend fun updateSettings(theme: String? = null, fontScale: Float? = null) {
        val current = userDao.observeSettings().first() ?: AppSettingsEntity()
        userDao.saveSettings(
            current.copy(
                theme = theme ?: current.theme,
                fontScale = fontScale ?: current.fontScale
            )
        )
    }

    suspend fun resetProgress() = userDao.clearProgress()

    suspend fun recordViewed(type: String, id: String) {
        contentDao.recordViewed(RecentlyViewedEntity(type, id))
    }

    fun isFavorite(type: String, id: String): Flow<Boolean> = userDao.observeFavorite(type, id)
}

data class LibraryContent(
    val commands: List<CommandEntity>,
    val scripts: List<ScriptEntity>,
    val tools: List<ToolEntity>,
    val tutorials: List<TutorialEntity>,
    val categories: List<CategoryEntity>
)

data class SearchResult(
    val type: String,
    val id: String,
    val title: String,
    val summary: String
)

fun LibraryContent.search(query: String): List<SearchResult> {
    val term = query.trim()
    if (term.isEmpty()) return emptyList()
    return buildList {
        commands.filter {
            listOf(it.title, it.command, it.category, it.description, it.tags, it.syntax, it.example, it.warning, it.relatedCommands).matches(term)
        }.forEach { add(SearchResult("command", it.id, it.title, "${it.category} · ${it.description}")) }
        scripts.filter {
            listOf(it.title, it.category, it.purpose, it.difficulty, it.code, it.explanation, it.howToRun, it.expectedOutput, it.safetyNotes).matches(term)
        }.forEach { add(SearchResult("script", it.id, it.title, "${it.category} · ${it.purpose}")) }
        tools.filter {
            listOf(it.name, it.category, it.description, it.purpose, it.installCommand, it.basicUsage, it.example, it.commonErrors, it.troubleshooting, it.safetyNote).matches(term)
        }.forEach { add(SearchResult("tool", it.id, it.name, "${it.category} · ${it.description}")) }
        tutorials.filter {
            listOf(it.title, it.level, it.section, it.body, it.tags).matches(term)
        }.forEach { add(SearchResult("tutorial", it.id, it.title, "${it.level} · ${it.section}")) }
        categories.filter {
            listOf(it.name, it.description, it.section).matches(term)
        }.forEach { add(SearchResult("category", it.name, it.name, it.description)) }
    }
}

private fun List<String>.matches(query: String): Boolean = any { it.contains(query, ignoreCase = true) }
