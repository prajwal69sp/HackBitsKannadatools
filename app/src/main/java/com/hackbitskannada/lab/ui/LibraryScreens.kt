package com.hackbitskannada.lab.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackbitskannada.lab.data.AppSettingsEntity
import com.hackbitskannada.lab.data.CategoryEntity
import com.hackbitskannada.lab.data.CommandEntity
import com.hackbitskannada.lab.data.FavoriteEntity
import com.hackbitskannada.lab.data.RecentlyViewedEntity
import com.hackbitskannada.lab.data.ScriptEntity
import com.hackbitskannada.lab.data.SearchResult
import com.hackbitskannada.lab.data.ToolEntity
import com.hackbitskannada.lab.data.TutorialEntity

@Composable
fun HomeScreen(
    commands: List<CommandEntity>,
    scripts: List<ScriptEntity>,
    tools: List<ToolEntity>,
    tutorials: List<TutorialEntity>,
    completedCount: Int,
    favoriteCount: Int,
    recentlyViewed: List<RecentlyViewedEntity>,
    categories: List<CategoryEntity>,
    onNavigate: (String) -> Unit,
    onOpen: (String, String) -> Unit,
    onSearch: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)))
                        .padding(20.dp)
                ) {
                    Text("HACKBITS / KANNADA", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace)
                    Text("Learn. Build.\nExplore. Secure.", modifier = Modifier.padding(top = 10.dp), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Termux & Linux Lab", modifier = Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("A practical offline field guide for your next build.", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable { onSearch("") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.Hub, contentDescription = null) },
                placeholder = { Text("Search commands, scripts, tools…") },
                shape = RoundedCornerShape(14.dp)
            )
        }
        item {
            LabCard {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Learning progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("$completedCount / ${tutorials.size} lessons completed", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    LinearProgressIndicator(
                        progress = { if (tutorials.isEmpty()) 0f else completedCount.toFloat() / tutorials.size },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    )
                    TextButton(onClick = { onNavigate("tutorials") }, modifier = Modifier.align(Alignment.End)) { Text("Continue learning") }
                }
            }
        }
        item { SectionHeader("Your lab") }
        val topics = listOf(
            Topic("Termux Commands", "pkg · Android shell", Icons.Filled.Terminal, "commands/${Uri.encode("Termux Basics")}"),
            Topic("Linux Commands", "Files · processes · permissions", Icons.Filled.Computer, "commands"),
            Topic("Bash Scripts", "Small, safe automations", Icons.Filled.Code, "scripts"),
            Topic("Python Scripts", "Read and adapt examples", Icons.Filled.Code, "scripts"),
            Topic("Networking", "DNS · SSH · HTTP", Icons.Filled.Hub, "commands/${Uri.encode("Networking")}"),
            Topic("Cybersecurity", "Authorized learning only", Icons.Filled.Security, "security"),
            Topic("Tools", "Install and troubleshoot", Icons.Filled.Tune, "tools"),
            Topic("Tutorials", "Three guided levels", Icons.Filled.Folder, "tutorials"),
            Topic("AI Command Assistant", "Local reference fallback", Icons.Filled.Psychology, "assistant"),
            Topic("Saved Commands", "$favoriteCount saved items", Icons.Filled.Bookmarks, "favorites")
        )
        items(topics.chunked(2)) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { topic ->
                    LabCard(modifier = Modifier.weight(1f), onClick = { onNavigate(topic.route) }) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(topic.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(topic.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(topic.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        item { SectionHeader("Recently viewed", "See all", { onNavigate("commands") }) }
        val recentTitles = recentlyViewed.mapNotNull { item ->
            when (item.contentType) {
                "command" -> commands.find { it.id == item.contentId }?.let { Triple("command", it.id, it.title) }
                "script" -> scripts.find { it.id == item.contentId }?.let { Triple("script", it.id, it.title) }
                "tool" -> tools.find { it.id == item.contentId }?.let { Triple("tool", it.id, it.name) }
                "tutorial" -> tutorials.find { it.id == item.contentId }?.let { Triple("tutorial", it.id, it.title) }
                else -> null
            }
        }
        if (recentTitles.isEmpty()) {
            item { Text("Open a command, script, tool, or lesson and it will appear here.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(recentTitles.take(4)) { (type, id, title) ->
                LabCard(onClick = { onOpen(type, id) }) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Open $title")
                    }
                }
            }
        }
        item { SafetyNotice() }
    }
}

@Composable
fun CommandsScreen(
    commands: List<CommandEntity>,
    categories: List<CategoryEntity>,
    favorites: List<FavoriteEntity>,
    initialCategory: String?,
    onOpen: (String, String) -> Unit,
    onFavorite: (String, String) -> Unit,
    onCopy: (String) -> Unit
) {
    var selectedCategory by rememberSaveable(initialCategory) { mutableStateOf(initialCategory ?: "All") }
    var selectedLevel by rememberSaveable { mutableStateOf("All") }
    val categoryNames = listOf("All") + categories.map { it.name }
    val visible = commands.filter { command ->
        (selectedCategory == "All" || command.category == selectedCategory) &&
            (selectedLevel == "All" || command.difficulty == selectedLevel)
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionHeader("Command reference", "${commands.size} entries") }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categoryNames.forEach { category ->
                    FilterChip(selected = selectedCategory == category, onClick = { selectedCategory = category }, label = { Text(category) })
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Beginner", "Intermediate", "Advanced").forEach { level ->
                    FilterChip(selected = selectedLevel == level, onClick = { selectedLevel = level }, label = { Text(level) })
                }
            }
        }
        if (visible.isEmpty()) item { EmptyState("No commands here yet", "Try a different category or difficulty.") }
        items(visible, key = { it.id }) { item ->
            ReferenceRow(
                title = item.title,
                subtitle = "${item.category} · ${item.description}",
                code = item.command,
                difficulty = item.difficulty,
                saved = favorites.any { it.contentType == "command" && it.contentId == item.id },
                onOpen = { onOpen("command", item.id) },
                onFavorite = { onFavorite("command", item.id) },
                onCopy = onCopy
            )
        }
    }
}

@Composable
fun ScriptsScreen(
    scripts: List<ScriptEntity>,
    favorites: List<FavoriteEntity>,
    onOpen: (String, String) -> Unit,
    onFavorite: (String, String) -> Unit,
    onCopy: (String) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionHeader("Script library", "${scripts.size} examples") }
        item { Text("Read, copy, and adapt each example. Nothing runs automatically.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(scripts, key = { it.id }) { script ->
            ReferenceRow(
                title = script.title,
                subtitle = "${script.category} · ${script.purpose}",
                code = script.code,
                difficulty = script.difficulty,
                saved = favorites.any { it.contentType == "script" && it.contentId == script.id },
                onOpen = { onOpen("script", script.id) },
                onFavorite = { onFavorite("script", script.id) },
                onCopy = onCopy
            )
        }
    }
}

@Composable
fun ToolsScreen(tools: List<ToolEntity>, onOpen: (String, String) -> Unit, onCopy: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionHeader("Tools & install guides", "${tools.size} guides") }
        item { SafetyNotice() }
        items(tools, key = { it.id }) { tool ->
            LabCard(onClick = { onOpen("tool", tool.id) }) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(tool.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(tool.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    CodeBlock(tool.installCommand, onCopy)
                }
            }
        }
    }
}

@Composable
fun SearchScreen(
    query: String,
    results: List<SearchResult>,
    onQueryChange: (String) -> Unit,
    onOpen: (String, String) -> Unit,
    onNavigate: (String) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search offline content") },
                placeholder = { Text("network, chmod, Python…") }
            )
        }
        if (query.isNotBlank()) item { SectionHeader("${results.size} results") }
        if (query.isNotBlank() && results.isEmpty()) item { EmptyState("No matches", "Try a shorter term or a related tag.") }
        items(results, key = { "${it.type}:${it.id}" }) { result ->
            LabCard(onClick = {
                if (result.type == "category") onNavigate("commands/${Uri.encode(result.id)}")
                else onOpen(result.type, result.id)
            }) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(result.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(result.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(result.type.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        if (query.isBlank()) item { EmptyState("Search across the lab", "Commands, scripts, tools, lessons, categories, and tags are indexed on this device.") }
    }
}

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteEntity>,
    commands: List<CommandEntity>,
    scripts: List<ScriptEntity>,
    tools: List<ToolEntity>,
    tutorials: List<TutorialEntity>,
    onOpen: (String, String) -> Unit,
    onFavorite: (String, String) -> Unit
) {
    val savedItems = favorites.mapNotNull { favorite ->
        val title = when (favorite.contentType) {
            "command" -> commands.find { it.id == favorite.contentId }?.title
            "script" -> scripts.find { it.id == favorite.contentId }?.title
            "tool" -> tools.find { it.id == favorite.contentId }?.name
            "tutorial" -> tutorials.find { it.id == favorite.contentId }?.title
            else -> null
        }
        title?.let { Triple(favorite.contentType, favorite.contentId, it) }
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionHeader("Saved learning", "${savedItems.size} items") }
        if (savedItems.isEmpty()) item { EmptyState("Nothing saved yet", "Use the bookmark control on a command, script, or lesson to keep it here.") }
        items(savedItems, key = { "${it.first}:${it.second}" }) { (type, id, title) ->
            LabCard(onClick = { onOpen(type, id) }) {
                Row(Modifier.padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                    IconButton(onClick = { onFavorite(type, id) }) { Icon(Icons.Filled.Bookmarks, contentDescription = "Remove $title from saved items") }
                }
            }
        }
    }
}

@Composable
fun TutorialsScreen(
    tutorials: List<TutorialEntity>,
    completed: List<com.hackbitskannada.lab.data.ProgressEntity>,
    onOpen: (String, String) -> Unit,
    onComplete: (String, Boolean) -> Unit
) {
    var selectedLevel by rememberSaveable { mutableStateOf("All") }
    val levels = listOf("All", "Beginner", "Intermediate", "Advanced")
    val visible = tutorials.filter { selectedLevel == "All" || it.level == selectedLevel }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionHeader("Learning paths", "${completed.size} / ${tutorials.size} complete") }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                levels.forEach { level -> FilterChip(selectedLevel == level, { selectedLevel = level }, label = { Text(level) }) }
            }
        }
        items(visible, key = { it.id }) { lesson ->
            val isComplete = completed.any { it.contentId == lesson.id }
            LabCard(onClick = { onOpen("tutorial", lesson.id) }) {
                Row(Modifier.padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${lesson.lessonOrder.toString().padStart(2, '0')} · ${lesson.title}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text("${lesson.level} · ${lesson.section}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onComplete(lesson.id, !isComplete) }) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = if (isComplete) "Mark incomplete" else "Mark complete", tint = if (isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityScreen(tutorials: List<TutorialEntity>, tools: List<ToolEntity>, onOpen: (String, String) -> Unit) {
    val lessons = tutorials.filter { it.section == "Security" }
    val securityTools = tools.filter { it.category == "Security learning" }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SafetyNotice() }
        item { Text("Practice in your own isolated lab, a CTF environment, or with explicit written permission.") }
        item { SectionHeader("Foundations", "${lessons.size} lessons") }
        items(lessons, key = { it.id }) { lesson ->
            LabCard(onClick = { onOpen("tutorial", lesson.id) }) {
                Column(Modifier.padding(14.dp)) {
                    Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                    Text(lesson.body, modifier = Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { SectionHeader("Tools in a lab") }
        items(securityTools, key = { it.id }) { tool ->
            LabCard(onClick = { onOpen("tool", tool.id) }) {
                Column(Modifier.padding(14.dp)) {
                    Text(tool.name, style = MaterialTheme.typography.titleMedium)
                    Text(tool.safetyNote, modifier = Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(completedCount: Int, lessonCount: Int, favoriteCount: Int, onNavigate: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            LabCard {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your learning lab", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("$completedCount / $lessonCount lessons completed · $favoriteCount saved items", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item { SafetyNotice() }
        item { ProfileAction("Learning paths", "Beginner to advanced", "tutorials", onNavigate) }
        item { ProfileAction("Saved items", "$favoriteCount bookmarked", "favorites", onNavigate) }
        item { ProfileAction("Ethical cybersecurity", "Legal labs and defensive learning", "security", onNavigate) }
        item { ProfileAction("About HackBitsKannada", "Topics, purpose, and contact placeholders", "about", onNavigate) }
        item { ProfileAction("Settings", "Theme, type size, and learning progress", "settings", onNavigate) }
    }
}

@Composable
private fun ProfileAction(title: String, subtitle: String, route: String, onNavigate: (String) -> Unit) {
    LabCard(onClick = { onNavigate(route) }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ArrowForward, contentDescription = "Open $title")
        }
    }
}

@Composable
fun AssistantScreen(answer: String, onAsk: (String) -> Unit) {
    var prompt by rememberSaveable { mutableStateOf("Explain chmod 755") }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            LabCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Local command helper", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("No AI service is connected. A small offline reference explains selected commands; other questions point you back to the local library.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(prompt, { prompt = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Ask about a command") }, minLines = 2)
                    Button(onClick = { onAsk(prompt) }, modifier = Modifier.fillMaxWidth()) { Text("Explain locally") }
                }
            }
        }
        if (answer.isNotBlank()) item {
            LabCard {
                Column(Modifier.padding(16.dp)) {
                    Text("LOCAL REFERENCE · NOT AI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(answer, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun AboutScreen() {
    val sections = listOf(
        "About HackBitsKannada" to "HackBitsKannada is a technology-focused learning platform created to make Termux, Linux, programming, AI tools, Android customization, web development, app development, and ethical hacking easier to understand. The platform focuses on practical learning through commands, scripts, tutorials, examples, and hands-on experimentation.",
        "What I Teach" to "Termux · Linux · Programming · AI tools · Web development · Android customization · Cybersecurity · Ethical hacking",
        "Termux" to "A mobile-first environment for learning shell basics and supported Linux packages on Android.",
        "Linux" to "Practical command-line foundations, permissions, processes, storage, services, and administration.",
        "Programming" to "Beginner-friendly examples across Python, Bash, JavaScript, Java, C/C++, and developer workflows.",
        "AI Tools" to "Educational introductions to AI-assisted development. This app's command helper is local reference content, not a connected AI service.",
        "Web Development" to "Build and understand websites, local development workflows, and secure coding practices.",
        "Android Customization" to "Explore Android settings and app development while respecting device security boundaries.",
        "Cybersecurity & Ethical Hacking" to "Cybersecurity content is intended for responsible learning, authorized testing, personal labs, and CTF environments.",
        "Contact / Social links" to "Social links: Not configured. Contact details will be added by the project owner."
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SafetyNotice() }
        items(sections, key = { it.first }) { (title, body) ->
            LabCard {
                Column(Modifier.padding(16.dp)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(body, modifier = Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(settings: AppSettingsEntity, onTheme: (String) -> Unit, onFontScale: (Float) -> Unit, onReset: () -> Unit) {
    var showReset by rememberSaveable { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            LabCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("DARK", "LIGHT", "SYSTEM").forEach { theme ->
                            FilterChip(selected = settings.theme == theme, onClick = { onTheme(theme) }, label = { Text(theme.lowercase().replaceFirstChar(Char::uppercase)) })
                        }
                    }
                    Text("Text size · ${(settings.fontScale * 100).toInt()}%", style = MaterialTheme.typography.titleSmall)
                    Slider(value = settings.fontScale, onValueChange = onFontScale, valueRange = 0.9f..1.2f, steps = 2)
                }
            }
        }
        item {
            LabCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Privacy & offline use", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Commands, scripts, tools, tutorials, search, saved items, and progress are stored on this device. The app does not execute copied commands or include an online AI connection.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            LabCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Disclaimer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Security lessons are for legal education, authorized testing, CTFs, and personal laboratories only.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Version 1.0.0", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        item {
            androidx.compose.material3.OutlinedButton(onClick = { showReset = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Reset learning progress")
            }
        }
    }
    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text("Reset lesson progress?") },
            text = { Text("Completed lesson markers will be cleared. Saved items and settings will stay unchanged.") },
            confirmButton = { TextButton(onClick = { onReset(); showReset = false }) { Text("Reset progress") } },
            dismissButton = { TextButton(onClick = { showReset = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun ReferenceRow(
    title: String,
    subtitle: String,
    code: String,
    difficulty: String,
    saved: Boolean,
    onOpen: () -> Unit,
    onFavorite: () -> Unit,
    onCopy: (String) -> Unit
) {
    LabCard(onClick = onOpen) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                DifficultyLabel(difficulty)
                IconButton(onClick = onFavorite) {
                    Icon(Icons.Filled.Bookmarks, contentDescription = if (saved) "Remove $title from saved items" else "Save $title", tint = if (saved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            CodeBlock(code, onCopy)
        }
    }
}

private data class Topic(val title: String, val subtitle: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)
