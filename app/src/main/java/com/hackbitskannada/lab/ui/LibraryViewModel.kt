package com.hackbitskannada.lab.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hackbitskannada.lab.data.AppSettingsEntity
import com.hackbitskannada.lab.data.LibraryContent
import com.hackbitskannada.lab.data.LibraryRepository
import com.hackbitskannada.lab.data.search
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LibraryRepository(application)
    private val sharing = SharingStarted.WhileSubscribed(5_000)

    val categories = repository.categories.stateIn(viewModelScope, sharing, emptyList())
    val commands = repository.commands.stateIn(viewModelScope, sharing, emptyList())
    val scripts = repository.scripts.stateIn(viewModelScope, sharing, emptyList())
    val tools = repository.tools.stateIn(viewModelScope, sharing, emptyList())
    val tutorials = repository.tutorials.stateIn(viewModelScope, sharing, emptyList())
    val favorites = repository.favorites.stateIn(viewModelScope, sharing, emptyList())
    val completedLessons = repository.completedLessons.stateIn(viewModelScope, sharing, emptyList())
    val recentlyViewed = repository.recentlyViewed.stateIn(viewModelScope, sharing, emptyList())
    val settings = repository.settings
        .combineSettings()
        .stateIn(viewModelScope, sharing, AppSettingsEntity())

    private val mutableSearchQuery = MutableStateFlow("")
    val searchQuery = mutableSearchQuery
    private val library = combine(commands, scripts, tools, tutorials, categories) { commandList, scriptList, toolList, tutorialList, categoryList ->
        LibraryContent(commandList, scriptList, toolList, tutorialList, categoryList)
    }
    val searchResults = combine(library, searchQuery) { content, query -> content.search(query) }
        .stateIn(viewModelScope, sharing, emptyList())

    private val mutableAssistantAnswer = MutableStateFlow("")
    val assistantAnswer = mutableAssistantAnswer

    init {
        viewModelScope.launch { repository.seedIfNeeded() }
    }

    fun setSearchQuery(value: String) {
        mutableSearchQuery.value = value
    }

    fun askAssistant(prompt: String) {
        mutableAssistantAnswer.value = EducationalFallback.explain(prompt)
    }

    fun toggleFavorite(type: String, id: String) {
        viewModelScope.launch { repository.toggleFavorite(type, id) }
    }

    fun recordViewed(type: String, id: String) {
        viewModelScope.launch { repository.recordViewed(type, id) }
    }

    fun setLessonCompleted(id: String, completed: Boolean) {
        viewModelScope.launch { repository.setLessonCompleted(id, completed) }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { repository.updateSettings(theme = theme) }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { repository.updateSettings(fontScale = scale) }
    }

    fun resetProgress() {
        viewModelScope.launch { repository.resetProgress() }
    }
}

private fun kotlinx.coroutines.flow.Flow<AppSettingsEntity?>.combineSettings() =
    map { it ?: AppSettingsEntity() }
