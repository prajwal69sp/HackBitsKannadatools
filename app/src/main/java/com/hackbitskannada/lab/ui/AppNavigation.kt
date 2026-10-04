package com.hackbitskannada.lab.ui

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hackbitskannada.lab.data.CommandEntity
import com.hackbitskannada.lab.data.FavoriteEntity
import com.hackbitskannada.lab.data.RecentlyViewedEntity
import kotlinx.coroutines.launch

@Composable
fun HackBitsApp(viewModel: LibraryViewModel = viewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    HackBitsTheme(settings.theme, settings.fontScale) {
        val navController = rememberNavController()
        val backStack by navController.currentBackStackEntryAsState()
        val route = backStack?.destination?.route ?: "home"
        val commands by viewModel.commands.collectAsStateWithLifecycle()
        val scripts by viewModel.scripts.collectAsStateWithLifecycle()
        val tools by viewModel.tools.collectAsStateWithLifecycle()
        val tutorials by viewModel.tutorials.collectAsStateWithLifecycle()
        val categories by viewModel.categories.collectAsStateWithLifecycle()
        val favorites by viewModel.favorites.collectAsStateWithLifecycle()
        val completed by viewModel.completedLessons.collectAsStateWithLifecycle()
        val recentlyViewed by viewModel.recentlyViewed.collectAsStateWithLifecycle()
        val query by viewModel.searchQuery.collectAsStateWithLifecycle()
        val results by viewModel.searchResults.collectAsStateWithLifecycle()
        val assistantAnswer by viewModel.assistantAnswer.collectAsStateWithLifecycle()
        val snackbar = remember { SnackbarHostState() }
        val clipboard = LocalClipboardManager.current
        val scope = rememberCoroutineScope()
        val openItem: (String, String) -> Unit = { type, id ->
            navController.navigate("detail/$type/${Uri.encode(id)}")
        }
        val copy: (String) -> Unit = { text ->
            clipboard.setText(AnnotatedString(text))
            scope.launch { snackbar.showSnackbar("Copied to clipboard") }
        }
        val topRoutes = setOf("home", "commands", "commands/{category}", "scripts", "tools", "profile")
        val showBottomBar = route in topRoutes

        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
            topBar = {
                AppTopBar(
                    title = screenTitle(route),
                    showBack = !showBottomBar,
                    onBack = { navController.popBackStack() },
                    onSearch = { navController.navigate("search") },
                    onFavorites = { navController.navigate("favorites") },
                    onAssistant = { navController.navigate("assistant") }
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        listOf(
                            Triple("home", "Home", Icons.Filled.Home),
                            Triple("commands", "Commands", Icons.Filled.Terminal),
                            Triple("scripts", "Scripts", Icons.Filled.Code),
                            Triple("tools", "Tools", Icons.Filled.Build),
                            Triple("profile", "Profile", Icons.Filled.Person)
                        ).forEach { (destination, label, icon) ->
                            val selected = route == destination || route == "commands/{category}" && destination == "commands"
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(destination) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(Modifier.padding(padding)) {
                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            commands, scripts, tools, tutorials, completed.size, favorites.size,
                            recentlyViewed, categories, onNavigate = { navController.navigate(it) },
                            onOpen = openItem,
                            onSearch = { value -> viewModel.setSearchQuery(value); navController.navigate("search") }
                        )
                    }
                    composable("commands") {
                        CommandsScreen(commands, categories, favorites, null, openItem, viewModel::toggleFavorite, copy)
                    }
                    composable("commands/{category}", arguments = listOf(navArgument("category") { type = NavType.StringType })) { entry ->
                        CommandsScreen(
                            commands, categories, favorites, Uri.decode(entry.arguments?.getString("category").orEmpty()),
                            openItem, viewModel::toggleFavorite, copy
                        )
                    }
                    composable("scripts") { ScriptsScreen(scripts, favorites, openItem, viewModel::toggleFavorite, copy) }
                    composable("tools") { ToolsScreen(tools, openItem, copy) }
                    composable("profile") {
                        ProfileScreen(
                            completed.size, tutorials.size, favorites.size,
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                    composable("search") {
                        SearchScreen(query, results, viewModel::setSearchQuery, openItem, navController::navigate)
                    }
                    composable("favorites") {
                        FavoritesScreen(favorites, commands, scripts, tools, tutorials, openItem, viewModel::toggleFavorite)
                    }
                    composable("tutorials") {
                        TutorialsScreen(tutorials, completed, openItem, viewModel::setLessonCompleted)
                    }
                    composable("security") { SecurityScreen(tutorials, tools, openItem) }
                    composable("assistant") { AssistantScreen(assistantAnswer, viewModel::askAssistant) }
                    composable("about") { AboutScreen() }
                    composable("settings") {
                        SettingsScreen(settings, viewModel::setTheme, viewModel::setFontScale, viewModel::resetProgress)
                    }
                    composable(
                        "detail/{kind}/{contentId}",
                        arguments = listOf(
                            navArgument("kind") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val kind = entry.arguments?.getString("kind").orEmpty()
                        val id = Uri.decode(entry.arguments?.getString("contentId").orEmpty())
                        ContentDetailScreen(
                            kind, id, commands, scripts, tools, tutorials, favorites, completed,
                            onFavorite = viewModel::toggleFavorite,
                            onComplete = viewModel::setLessonCompleted,
                            onViewed = viewModel::recordViewed,
                            onCopy = copy,
                            onOpen = openItem
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppTopBar(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onFavorites: () -> Unit,
    onAssistant: () -> Unit
) {
    androidx.compose.material3.TopAppBar(
        title = {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium)
                if (title == "HackBitsKannada") {
                    Text("Learn. Build. Explore. Secure.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            IconButton(onClick = onSearch) { Icon(Icons.Filled.Search, contentDescription = "Search") }
            IconButton(onClick = onFavorites) { Icon(Icons.Filled.Bookmarks, contentDescription = "Saved items") }
            IconButton(onClick = onAssistant) { Icon(Icons.Filled.SmartToy, contentDescription = "Local command assistant") }
        }
    )
}

private fun screenTitle(route: String): String = when (route) {
    "home" -> "HackBitsKannada"
    "commands", "commands/{category}" -> "Command library"
    "scripts" -> "Script library"
    "tools" -> "Tools & guides"
    "profile" -> "Your learning lab"
    "search" -> "Search offline library"
    "favorites" -> "Saved items"
    "tutorials" -> "Learning paths"
    "security" -> "Ethical security"
    "assistant" -> "Command assistant"
    "about" -> "About HackBitsKannada"
    "settings" -> "Settings"
    else -> "Learning reference"
}
