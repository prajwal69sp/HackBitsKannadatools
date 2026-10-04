package com.hackbitskannada.lab.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hackbitskannada.lab.data.CommandEntity
import com.hackbitskannada.lab.data.FavoriteEntity
import com.hackbitskannada.lab.data.ProgressEntity
import com.hackbitskannada.lab.data.ScriptEntity
import com.hackbitskannada.lab.data.ToolEntity
import com.hackbitskannada.lab.data.TutorialEntity

@Composable
fun ContentDetailScreen(
    kind: String,
    id: String,
    commands: List<CommandEntity>,
    scripts: List<ScriptEntity>,
    tools: List<ToolEntity>,
    tutorials: List<TutorialEntity>,
    favorites: List<FavoriteEntity>,
    completed: List<ProgressEntity>,
    onFavorite: (String, String) -> Unit,
    onComplete: (String, Boolean) -> Unit,
    onViewed: (String, String) -> Unit,
    onCopy: (String) -> Unit,
    onOpen: (String, String) -> Unit
) {
    LaunchedEffect(kind, id) { onViewed(kind, id) }
    val saved = favorites.any { it.contentType == kind && it.contentId == id }
    val command = commands.firstOrNull { it.id == id }
    val script = scripts.firstOrNull { it.id == id }
    val tool = tools.firstOrNull { it.id == id }
    val tutorial = tutorials.firstOrNull { it.id == id }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (kind) {
            "command" -> if (command != null) commandDetail(command, saved, onFavorite, onCopy)
            "script" -> if (script != null) scriptDetail(script, saved, onFavorite, onCopy)
            "tool" -> if (tool != null) toolDetail(tool, onCopy)
            "tutorial" -> if (tutorial != null) tutorialDetail(tutorial, saved, completed.any { it.contentId == id }, onFavorite, onComplete)
        }
        if (command == null && script == null && tool == null && tutorial == null) {
            item { EmptyState("Content unavailable", "This item is no longer in the local library.") }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.commandDetail(
    item: CommandEntity,
    saved: Boolean,
    onFavorite: (String, String) -> Unit,
    onCopy: (String) -> Unit
) {
    item { DetailHeading(item.title, "${item.category} · ${item.difficulty}", saved) { onFavorite("command", item.id) } }
    item { Text(item.description, style = MaterialTheme.typography.bodyLarge) }
    item { SectionHeader("Syntax") }
    item { CodeBlock(item.syntax, onCopy) }
    item { SectionHeader("Example") }
    item { CodeBlock(item.example, onCopy) }
    item { DetailSection("Expected usage", item.expectedUsage) }
    if (item.warning.isNotBlank()) item {
        SectionHeader("Take care")
        SafetyNotice(item.warning)
    }
    if (item.relatedCommands.isNotBlank()) item { DetailSection("Related commands", item.relatedCommands) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.scriptDetail(
    script: ScriptEntity,
    saved: Boolean,
    onFavorite: (String, String) -> Unit,
    onCopy: (String) -> Unit
) {
    item { DetailHeading(script.title, "${script.category} · ${script.difficulty}", saved) { onFavorite("script", script.id) } }
    item { DetailSection("Purpose", script.purpose) }
    item { SectionHeader("Full script") }
    item { CodeBlock(script.code, onCopy) }
    item { DetailSection("How it works", script.explanation) }
    item { DetailSection("How to run", script.howToRun) }
    item { SectionHeader("Expected output") }
    item { CodeBlock(script.expectedOutput, onCopy) }
    item { SafetyNotice(script.safetyNotes) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.toolDetail(tool: ToolEntity, onCopy: (String) -> Unit) {
    item { DetailHeading(tool.name, tool.category, false, null) }
    item { DetailSection("About", tool.description) }
    item { DetailSection("Purpose", tool.purpose) }
    item { SectionHeader("Installation") }
    item { CodeBlock(tool.installCommand, onCopy) }
    item { DetailSection("Basic usage", tool.basicUsage) }
    item { SectionHeader("Example") }
    item { CodeBlock(tool.example, onCopy) }
    item { DetailSection("Common errors", tool.commonErrors) }
    item { DetailSection("Troubleshooting", tool.troubleshooting) }
    item { SafetyNotice(tool.safetyNote) }
}

private fun androidx.compose.foundation.lazy.LazyListScope.tutorialDetail(
    tutorial: TutorialEntity,
    saved: Boolean,
    isComplete: Boolean,
    onFavorite: (String, String) -> Unit,
    onComplete: (String, Boolean) -> Unit
) {
    item { DetailHeading(tutorial.title, "${tutorial.level} · ${tutorial.section}", saved) { onFavorite("tutorial", tutorial.id) } }
    item { Text(tutorial.body, style = MaterialTheme.typography.bodyLarge) }
    item { Text("Topics: ${tutorial.tags.replace(',', ' ')}", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    if (tutorial.section == "Security" || tutorial.tags.contains("security")) item { SafetyNotice() }
    item {
        Button(onClick = { onComplete(tutorial.id, !isComplete) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null)
            Text(if (isComplete) "  Mark as incomplete" else "  Mark lesson complete")
        }
    }
}

@Composable
private fun DetailHeading(title: String, subtitle: String, saved: Boolean, onFavorite: (() -> Unit)?) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (onFavorite != null) {
            IconButton(onClick = onFavorite) {
                Icon(Icons.Filled.Bookmarks, contentDescription = if (saved) "Remove from saved items" else "Save item", tint = if (saved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
