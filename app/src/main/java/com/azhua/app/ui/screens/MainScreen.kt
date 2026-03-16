package com.azhua.app.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.azhua.app.ui.extension.ExtensionScreen
import com.azhua.app.ui.navigation.Screen
import com.azhua.app.ui.screens.detail.DetailScreen
import com.azhua.app.ui.screens.discover.DiscoverViewModel
import com.azhua.app.ui.screens.history.HistoryScreen
import com.azhua.app.ui.screens.player.PlayerScreen
import com.azhua.app.ui.theme.JadeGreen
import com.azhua.app.ui.theme.ObsidianBlack
import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.Episode
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * MainScreen - Gerbang Utama Sekte
 * 
 * Layar utama aplikasi dengan Bottom Navigation dan Navigation Graph lengkap:
 * - Pusaka (Library): Koleksi anime pengguna
 * - Jelajah (Discover): Katalog dan pencarian
 * - Riwayat (History): History tontonan
 * - Paviliun (Extensions): Repository dan manajemen ekstensi
 * - Detail: Detail anime dan daftar episode
 * - Player: Pemutar video fullscreen
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val discoverViewModel: DiscoverViewModel = viewModel()

    Scaffold(
        bottomBar = {
            // Hanya tampilkan bottom bar untuk screen utama
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            
            val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }
            
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp
                ) {
                    val currentDestination = navBackStackEntry?.destination

                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == screen.route 
                        } == true

                        NavigationBarItem(
                            icon = {
                                androidx.compose.material3.Icon(
                                    imageVector = if (selected) screen.iconSelected else screen.iconUnselected,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianBlack,
                                selectedTextColor = JadeGreen,
                                indicatorColor = JadeGreen,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            alwaysShowLabel = true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Library.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            // 🏛️ Pusaka - Library/Koleksi
            composable(Screen.Library.route) { 
                PlaceholderScreen(
                    title = "📚 Pusaka",
                    subtitle = "Koleksi Donghua Anda akan muncul di sini"
                ) 
            }

            // 🔍 Jelajah - Discover/Katalog
            composable(Screen.Discover.route) { 
                val activeSource = discoverViewModel.selectedSource.collectAsState().value
                
                com.azhua.app.ui.screens.discover.DiscoverScreen(
                    viewModel = discoverViewModel,
                    onAnimeClick = { anime ->
                        activeSource?.let { source ->
                            // Encode URL agar aman dilempar via NavRoute
                            val encodedUrl = URLEncoder.encode(
                                anime.sourceUrl.ifEmpty { anime.id },
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate("detail/$encodedUrl")
                        }
                    },
                    onNavigateToExtensions = {
                        navController.navigate(Screen.Extensions.route) {
                            launchSingleTop = true
                        }
                    }
                ) 
            }

            // ⏱️ Riwayat - History
            composable(Screen.History.route) { 
                HistoryScreen(
                    onAnimeClick = { history ->
                        // Navigate to detail dengan anime dari history
                        val encodedUrl = URLEncoder.encode(
                            history.animeUrl,
                            StandardCharsets.UTF_8.toString()
                        )
                        navController.navigate("detail/$encodedUrl")
                    }
                )
            }

            // 📦 Paviliun - Extensions
            composable(Screen.Extensions.route) { 
                ExtensionScreen()
            }

            // 🎬 Detail Screen
            composable(
                route = "detail/{encodedUrl}",
                arguments = listOf(
                    navArgument("encodedUrl") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val encodedUrl = backStackEntry.arguments?.getString("encodedUrl") ?: ""
                val url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
                
                val activeSource = discoverViewModel.selectedSource.collectAsState().value
                val popularAnime = discoverViewModel.popularAnime.collectAsState().value
                
                // Cari anime dari list popular atau buat dummy
                val initialAnime = popularAnime.find { it.sourceUrl == url || it.id == url } 
                    ?: Anime(
                        id = url,
                        title = "Memuat...",
                        sourceUrl = url
                    )
                
                if (activeSource != null) {
                    DetailScreen(
                        url = url,
                        source = activeSource,
                        initialAnime = initialAnime,
                        onBackClick = { navController.popBackStack() },
                        onEpisodeClick = { episode ->
                            // Navigate to player dengan data lengkap
                            val encodedEpUrl = URLEncoder.encode(
                                episode.sourceUrl.ifEmpty { episode.id },
                                StandardCharsets.UTF_8.toString()
                            )
                            val encodedAnimeUrl = URLEncoder.encode(
                                initialAnime.sourceUrl.ifEmpty { initialAnime.id },
                                StandardCharsets.UTF_8.toString()
                            )
                            val encodedTitle = URLEncoder.encode(
                                initialAnime.title,
                                StandardCharsets.UTF_8.toString()
                            )
                            val encodedCover = URLEncoder.encode(
                                initialAnime.coverImage,
                                StandardCharsets.UTF_8.toString()
                            )
                            navController.navigate(
                                "player/$encodedEpUrl/$encodedAnimeUrl/$encodedTitle/$encodedCover"
                            )
                        }
                    )
                } else {
                    PlaceholderScreen(
                        title = "Error",
                        subtitle = "Tidak ada ekstensi aktif"
                    )
                }
            }

            // ▶️ Player Screen
            composable(
                route = "player/{encodedEpUrl}/{encodedAnimeUrl}/{encodedTitle}/{encodedCover}",
                arguments = listOf(
                    navArgument("encodedEpUrl") { type = NavType.StringType },
                    navArgument("encodedAnimeUrl") { type = NavType.StringType },
                    navArgument("encodedTitle") { type = NavType.StringType },
                    navArgument("encodedCover") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val encodedEpUrl = backStackEntry.arguments?.getString("encodedEpUrl") ?: ""
                val encodedAnimeUrl = backStackEntry.arguments?.getString("encodedAnimeUrl") ?: ""
                val encodedTitle = backStackEntry.arguments?.getString("encodedTitle") ?: ""
                val encodedCover = backStackEntry.arguments?.getString("encodedCover") ?: ""
                
                val episodeUrl = URLDecoder.decode(encodedEpUrl, StandardCharsets.UTF_8.toString())
                val animeUrl = URLDecoder.decode(encodedAnimeUrl, StandardCharsets.UTF_8.toString())
                val animeTitle = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())
                val animeCover = URLDecoder.decode(encodedCover, StandardCharsets.UTF_8.toString())
                
                val activeSource = discoverViewModel.selectedSource.collectAsState().value
                
                if (activeSource != null) {
                    PlayerScreen(
                        episode = Episode(
                            id = episodeUrl,
                            number = 1,
                            title = "Episode",
                            sourceUrl = episodeUrl
                        ),
                        animeUrl = animeUrl,
                        animeTitle = animeTitle,
                        animeCover = animeCover,
                        source = activeSource,
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tidak ada ekstensi aktif")
                    }
                }
            }
        }
    }
}

/**
 * Layar placeholder untuk fitur yang belum diimplementasikan
 */
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String = ""
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = JadeGreen,
                textAlign = TextAlign.Center
            )
            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
