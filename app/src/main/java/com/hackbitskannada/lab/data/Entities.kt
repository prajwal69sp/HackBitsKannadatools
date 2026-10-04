package com.hackbitskannada.lab.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val name: String,
    val description: String,
    val section: String
)

@Entity(tableName = "commands")
data class CommandEntity(
    @PrimaryKey val id: String,
    val title: String,
    val command: String,
    val category: String,
    val description: String,
    val syntax: String,
    val example: String,
    val expectedUsage: String,
    val difficulty: String,
    val tags: String,
    val warning: String = "",
    val relatedCommands: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scripts")
data class ScriptEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val purpose: String,
    val code: String,
    val explanation: String,
    val howToRun: String,
    val expectedOutput: String,
    val safetyNotes: String
)

@Entity(tableName = "tools")
data class ToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val purpose: String,
    val installCommand: String,
    val basicUsage: String,
    val example: String,
    val commonErrors: String,
    val troubleshooting: String,
    val safetyNote: String
)

@Entity(tableName = "tutorials")
data class TutorialEntity(
    @PrimaryKey val id: String,
    val title: String,
    val level: String,
    val section: String,
    val lessonOrder: Int,
    val body: String,
    val tags: String
)

@Entity(tableName = "favorites", primaryKeys = ["contentType", "contentId"])
data class FavoriteEntity(
    val contentType: String,
    val contentId: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "progress", primaryKeys = ["contentType", "contentId"])
data class ProgressEntity(
    val contentType: String,
    val contentId: String,
    val completed: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_viewed", primaryKeys = ["contentType", "contentId"])
data class RecentlyViewedEntity(
    val contentType: String,
    val contentId: String,
    val viewedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val theme: String = "SYSTEM",
    val fontScale: Float = 1f
)