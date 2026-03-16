package com.azhua.app.ui.screens.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.azhua.app.ui.theme.ImperialGold
import com.azhua.app.ui.theme.JadeGreen
import com.azhua.app.ui.theme.ObsidianBlack
import com.azhua.core.contracts.Source
import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.AnimeStatus
import com.azhua.core.contracts.models.Episode

/**
 * DetailScreen - Aula Rincian
 *
 * Layar untuk menampilkan detail lengkap anime dan daftar episode.
 * User bisa melihat informasi anime dan memilih episode untuk ditonton.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    url: String,
    source: Source,
    initialAnime: Anime,
    onBackClick: () -> Unit,
    onEpisodeClick: (Episode) -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val anime by viewModel.animeDetail.collectAsState()
    val episodes by viewModel.episodes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(url) {
        viewModel.loadAnimeDetails(source, url, initialAnime)
    }

    val currentAnime = anime ?: initialAnime

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Donghua") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.refresh(source, url, initialAnime)
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header Section (Cover & Info)
            item {
                HeaderSection(anime = currentAnime)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Description Section
            item {
                DescriptionSection(anime = currentAnime)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Episode List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daftar Episode (${episodes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Error State
            if (error != null) {
                item {
                    ErrorSection(message = error!!) {
                        viewModel.refresh(source, url, initialAnime)
                    }
                }
            }

            // Episode List
            if (episodes.isEmpty() && !isLoading) {
                item {
                    EmptyEpisodesSection()
                }
            } else {
                items(
                    items = episodes.sortedBy { it.number },
                    key = { it.id }
                ) { episode ->
                    EpisodeItem(
                        episode = episode,
                        onClick = { onEpisodeClick(episode) }
                    )
                }
            }
        }
    }
}

/**
 * Header section dengan cover image dan informasi utama.
 */
@Composable
fun HeaderSection(anime: Anime, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        // Cover Image
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.width(140.dp)
        ) {
            Box {
                AsyncImage(
                    model = anime.coverImage.ifEmpty {
                        "https://via.placeholder.com/300x450/1F2833/45A29E?text=${anime.title.take(2)}"
                    },
                    contentDescription = anime.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                )

                // Status Badge
                val statusColor = when (anime.status) {
                    AnimeStatus.ONGOING -> ImperialGold
                    AnimeStatus.COMPLETED -> JadeGreen
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = when (anime.status) {
                            AnimeStatus.ONGOING -> "ONGOING"
                            AnimeStatus.COMPLETED -> "COMPLETED"
                            AnimeStatus.UPCOMING -> "UPCOMING"
                            else -> anime.status.name
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info Column
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Alternative Titles
            if (anime.alternativeTitles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = anime.alternativeTitles.first(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Meta Info
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Rating
                if (anime.rating > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ImperialGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${anime.rating}/10",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Year
                if (anime.releaseYear > 0) {
                    InfoRow(label = "Tahun", value = anime.releaseYear.toString())
                }

                // Type
                InfoRow(label = "Tipe", value = anime.type.name)

                // Language
                InfoRow(label = "Bahasa", value = anime.sourceUrl.takeIf { it.isNotEmpty() }?.let { "ID" } ?: "-")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Genres
            if (anime.genres.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    anime.genres.take(3).forEach { genre ->
                        GenreChip(genre = genre)
                    }
                }
            }
        }
    }
}

/**
 * Row untuk menampilkan info label dan value.
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Chip untuk genre.
 */
@Composable
fun GenreChip(genre: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Section untuk deskripsi anime.
 */
@Composable
fun DescriptionSection(anime: Anime) {
    Column {
        Text(
            text = "Sinopsis",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = anime.description.ifEmpty { "Tidak ada deskripsi." },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2f
        )
    }
}

/**
 * Item untuk satu episode dalam list.
 */
@Composable
fun EpisodeItem(episode: Episode, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Episode Number Badge
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = episode.number.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Episode Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title.ifEmpty { "Episode ${episode.number}" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )

                if (episode.duration > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatDuration(episode.duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Play Icon
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Format detik menjadi mm:ss atau HH:mm:ss.
 */
fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}

/**
 * Section saat tidak ada episode.
 */
@Composable
fun EmptyEpisodesSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tidak ada episode tersedia.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Section saat terjadi error.
 */
@Composable
fun ErrorSection(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚠️ $message",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Coba Lagi")
        }
    }
}
