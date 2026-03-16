package com.azhua.app.ui.screens.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.azhua.app.ui.theme.ImperialGold
import com.azhua.app.ui.theme.JadeGreen
import com.azhua.app.ui.theme.LightJade
import com.azhua.app.ui.theme.ObsidianBlack
import com.azhua.core.contracts.Source
import com.azhua.core.contracts.models.Anime

/**
 * DiscoverScreen - Tab Jelajah
 *
 * Menampilkan daftar Donghua dari ekstensi yang aktif dalam bentuk Grid.
 * Menggunakan data dari DiscoverViewModel yang terhubung ke ExtensionLoader.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = viewModel(),
    onAnimeClick: (Anime) -> Unit = {},
    onNavigateToExtensions: () -> Unit = {}
) {
    val selectedSource by viewModel.selectedSource.collectAsState()
    val popularAnime by viewModel.popularAnime.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val activeSources by viewModel.activeSources.collectAsState()
    val error by viewModel.error.collectAsState()

    var showSourceDropdown by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari Donghua...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = ObsidianBlack,
                                unfocusedContainerColor = ObsidianBlack,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            viewModel.searchAnime(searchQuery)
                                        }
                                        isSearching = false
                                    }
                                ) {
                                    Icon(Icons.Default.Search, "Search")
                                }
                            }
                        )
                    } else {
                        // Source Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showSourceDropdown = true }
                        ) {
                            Text(
                                text = selectedSource?.name ?: "Pilih Sumber",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Source",
                                tint = MaterialTheme.colorScheme.primary
                            )

                            // Dropdown menu untuk pilih sumber
                            DropdownMenu(
                                expanded = showSourceDropdown,
                                onDismissRequest = { showSourceDropdown = false }
                            ) {
                                activeSources.forEach { source ->
                                    DropdownMenuItem(
                                        text = {
                                            Row {
                                                Text(source.name)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = source.language.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectSource(source)
                                            showSourceDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    if (!isSearching) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.refreshExtensions() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Belum ada ekstensi
                activeSources.isEmpty() -> {
                    EmptyExtensionState(onNavigateToExtensions = onNavigateToExtensions)
                }

                // Loading
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Error
                error != null -> {
                    ErrorState(
                        message = error!!,
                        onRetry = { viewModel.refreshExtensions() }
                    )
                }

                // Content
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 120.dp),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = popularAnime,
                            key = { it.id }
                        ) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onAnimeClick(anime) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card untuk menampilkan satu anime dalam grid.
 */
@Composable
fun AnimeCard(
    anime: Anime,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Cover Image
            AsyncImage(
                model = anime.coverImage.ifEmpty {
                    "https://via.placeholder.com/300x450/1F2833/45A29E?text=${anime.title.take(2)}"
                },
                contentDescription = anime.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient overlay di bawah
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color.Transparent,
                                ObsidianBlack.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Status Badge (ONGOING/COMPLETED)
            if (anime.status.name != "UNKNOWN") {
                Surface(
                    color = when (anime.status.name) {
                        "ONGOING" -> ImperialGold.copy(alpha = 0.9f)
                        "COMPLETED" -> JadeGreen.copy(alpha = 0.9f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = when (anime.status.name) {
                            "ONGOING" -> "ONGOING"
                            "COMPLETED" -> "END"
                            else -> anime.status.name
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Title & Info di bawah
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            ) {
                Text(
                    text = anime.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )

                if (anime.releaseYear > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = anime.releaseYear.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * State saat belum ada ekstensi terinstal.
 */
@Composable
fun EmptyExtensionState(
    onNavigateToExtensions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Extension,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "🏛️ Belum Ada Artefak",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Anda belum menginstal ekstensi apapun.\nKunjungi Paviliun Kitab untuk menambah sumber Donghua.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToExtensions,
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Extension,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kunjungi Paviliun")
        }
    }
}

/**
 * State saat terjadi error.
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Terjadi Kesalahan",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Coba Lagi")
        }
    }
}
