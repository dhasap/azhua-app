package com.azhua.app.ui.extension

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.azhua.app.data.models.ExtensionItem
import kotlinx.coroutines.launch

/**
 * Paviliun Kitab - Layar Repository Ekstensi
 * 
 * Menampilkan daftar ekstensi yang tersedia untuk diunduh dan diinstal
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionScreen(
    viewModel: ExtensionViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val extensions = viewModel.extensions.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.error.value
    val selectedTab = viewModel.selectedTab.value
    val downloadingIds = viewModel.downloadingIds.value
    
    val filteredExtensions = viewModel.getFilteredExtensions()
    
    // Show error in snackbar
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "🏛️ Paviliun Kitab",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Repository Ekstensi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadExtensions() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                ExtensionViewModel.ExtensionTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { viewModel.selectTab(tab) },
                        text = {
                            val count = when (tab) {
                                ExtensionViewModel.ExtensionTab.ALL -> extensions.size
                                ExtensionViewModel.ExtensionTab.INSTALLED -> 
                                    extensions.count { viewModel.isInstalled(it.pkg) }
                                ExtensionViewModel.ExtensionTab.AVAILABLE -> 
                                    extensions.count { !viewModel.isInstalled(it.pkg) }
                            }
                            Text("${tab.label} ($count)")
                        }
                    )
                }
            }
            
            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    filteredExtensions.isEmpty() -> {
                        EmptyState(
                            message = when (selectedTab) {
                                ExtensionViewModel.ExtensionTab.ALL -> "Tidak ada ekstensi tersedia"
                                ExtensionViewModel.ExtensionTab.INSTALLED -> "Belum ada ekstensi terinstal"
                                ExtensionViewModel.ExtensionTab.AVAILABLE -> "Semua ekstensi sudah terinstal"
                            },
                            onRefresh = { viewModel.loadExtensions() }
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredExtensions) { extension ->
                                ExtensionCard(
                                    extension = extension,
                                    isInstalled = viewModel.isInstalled(extension.pkg),
                                    installedVersion = viewModel.getInstalledVersion(extension.pkg),
                                    needsUpdate = viewModel.needsUpdate(extension),
                                    isDownloading = downloadingIds.contains(extension.pkg),
                                    onDownload = { viewModel.downloadExtension(extension) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExtensionCard(
    extension: ExtensionItem,
    isInstalled: Boolean,
    installedVersion: String?,
    needsUpdate: Boolean,
    isDownloading: Boolean,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isInstalled) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (extension.iconUrl.isNotBlank()) {
                    AsyncImage(
                        model = extension.iconUrl,
                        contentDescription = extension.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = extension.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = extension.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = extension.displayVersion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Language badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = extension.lang.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                // Installed version info
                if (isInstalled && installedVersion != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Terinstal: v$installedVersion ${if (needsUpdate) "(Update tersedia)" else "✓"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (needsUpdate) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Action Button
            when {
                isDownloading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp
                    )
                }
                needsUpdate -> {
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Update,
                            contentDescription = "Update",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                isInstalled -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Terinstal",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                else -> {
                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📚",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Muat Ulang")
        }
    }
}

// Extension for tab labels
val ExtensionViewModel.ExtensionTab.label: String
    get() = when (this) {
        ExtensionViewModel.ExtensionTab.ALL -> "Semua"
        ExtensionViewModel.ExtensionTab.INSTALLED -> "Terinstal"
        ExtensionViewModel.ExtensionTab.AVAILABLE -> "Tersedia"
    }
