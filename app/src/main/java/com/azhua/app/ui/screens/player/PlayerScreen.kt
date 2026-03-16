package com.azhua.app.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.azhua.app.data.local.AppDatabase
import com.azhua.app.data.local.WatchHistory
import com.azhua.app.ui.theme.ObsidianBlack
import com.azhua.core.contracts.Source
import com.azhua.core.contracts.models.Episode
import com.azhua.core.contracts.models.VideoQuality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * PlayerScreen - Cermin Ilahi
 *
 * Layar fullscreen untuk memutar video menggunakan ExoPlayer (Media3).
 * Mendukung HLS (.m3u8) dan MP4 streaming.
 * Menyimpan riwayat tontonan ke Room Database saat ditutup.
 */
private const val TAG = "PlayerScreen"

@Composable
fun PlayerScreen(
    episode: Episode,
    animeUrl: String, // URL anime utama (untuk history)
    animeTitle: String, // Judul anime
    animeCover: String, // Cover anime
    source: Source, 
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()
    
    // Database untuk menyimpan riwayat
    val db = AppDatabase.getDatabase(context)
    val historyDao = db.historyDao()

    var videoStreams by remember { mutableStateOf<List<com.azhua.core.contracts.models.VideoStream>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val setError: (String?) -> Unit = { error = it }
    var isFullscreen by remember { mutableStateOf(true) }

    // ExoPlayer Instance
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context))
            .build()
            .apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
    }

    // Lifecycle observer untuk pause/play saat app background/foreground
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Mengambil URL video dari ekstensi
    LaunchedEffect(episode.id) {
        Log.d(TAG, "Mengambil video streams untuk episode: ${episode.id}")
        isLoading = true
        error = null

        try {
            val streams = source.getVideoStreams(episode.id)
            videoStreams = streams
            Log.d(TAG, "Berhasil mendapat ${streams.size} stream")

            if (streams.isNotEmpty()) {
                // Pilih stream terbaik (prioritas FHD > HD > SD)
                val bestStream = streams.minByOrNull {
                    when (it.quality) {
                        VideoQuality.UHD_4K -> 1
                        VideoQuality.FHD_1080P -> 2
                        VideoQuality.HD_720P -> 3
                        VideoQuality.SD_480P -> 4
                        VideoQuality.SD_360P -> 5
                        else -> 6
                    }
                } ?: streams.first()

                Log.d(TAG, "Memutar stream: ${bestStream.quality} - ${bestStream.url}")

                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.parse(bestStream.url))
                    .build()

                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            } else {
                error = "Tidak ada stream video tersedia"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil video streams", e)
            error = "Gagal memuat video: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Player Event Listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> isLoading = true
                    Player.STATE_READY -> isLoading = false
                    Player.STATE_ENDED -> Log.d(TAG, "Playback ended")
                    Player.STATE_IDLE -> Log.d(TAG, "Playback idle")
                }
            }

            override fun onPlayerError(playbackError: PlaybackException) {
                Log.e(TAG, "Player error: ${playbackError.errorCodeName}", playbackError)
                setError("Error memutar video: ${playbackError.message}")
                isLoading = false
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // Cleanup saat screen ditutup + SIMPAN RIWAYAT 🔥
    DisposableEffect(Unit) {
        onDispose {
            Log.d(TAG, "Melepas ExoPlayer dan menyimpan riwayat")
            
            // SIMPAN RIWAYAT SAAT PLAYER DITUTUP
            val currentPos = exoPlayer.currentPosition
            val totalDur = exoPlayer.duration
            
            // Jangan simpan jika baru nonton 2 detik (cegah spam)
            if (currentPos > 2000L) {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        historyDao.insertOrUpdate(
                            WatchHistory(
                                animeUrl = animeUrl,
                                title = animeTitle,
                                coverUrl = animeCover,
                                sourceName = source.name,
                                episodeUrl = episode.sourceUrl,
                                episodeName = episode.title,
                                timestampMs = currentPos,
                                durationMs = if (totalDur > 0) totalDur else 0L
                            )
                        )
                        Log.d(TAG, "Riwayat tersimpan: $animeTitle - ${WatchHistory.formatDuration(currentPos)}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Gagal menyimpan riwayat", e)
                    }
                }
            }
            
            exoPlayer.release()
        }
    }

    // Fullscreen handling
    LaunchedEffect(isFullscreen) {
        activity?.let {
            val windowInsetsController = it.window.insetsController
            if (isFullscreen) {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                windowInsetsController?.hide(android.view.WindowInsets.Type.systemBars())
            } else {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                windowInsetsController?.show(android.view.WindowInsets.Type.systemBars())
            }
        }
    }

    // Reset orientation saat keluar
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        when {
            // Error State
            error != null -> {
                ErrorOverlay(
                    message = error!!,
                    onBackClick = onBackClick,
                    onRetry = {
                        error = null
                        isLoading = true
                        // Retry logic
                    }
                )
            }

            // Player
            videoStreams.isNotEmpty() -> {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = true
                            controllerHideOnTouch = true
                            controllerAutoShow = true
                            controllerShowTimeoutMs = 3000

                            // Layout params fullscreen
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            // Set resize mode to fill screen
                            resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading Overlay
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Memuat video...",
                                color = Color.White
                            )
                        }
                    }
                }

                // Back Button Overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                }
            }

            // Loading State
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Mengambil link video...",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Overlay untuk menampilkan error.
 */
@Composable
fun ErrorOverlay(
    message: String,
    onBackClick: () -> Unit,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "⚠️",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Terjadi Kesalahan",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                onClick = onRetry,
                color = MaterialTheme.colorScheme.primary,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Coba Lagi",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    color = ObsidianBlack,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
