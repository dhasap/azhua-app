package com.azhua.app.ui.extension

import android.app.Application
import android.content.BroadcastReceiver
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.azhua.app.data.managers.ExtensionManager
import com.azhua.app.data.models.ExtensionItem
import kotlinx.coroutines.launch

/**
 * ViewModel untuk layar Paviliun Kitab (Extension Repository)
 */
class ExtensionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val manager = ExtensionManager(application)
    private var downloadReceiver: BroadcastReceiver? = null
    
    // State untuk UI
    private val _extensions = mutableStateOf<List<ExtensionItem>>(emptyList())
    val extensions: State<List<ExtensionItem>> = _extensions
    
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error
    
    private val _selectedTab = mutableStateOf(ExtensionTab.ALL)
    val selectedTab: State<ExtensionTab> = _selectedTab
    
    private val _downloadingIds = mutableStateOf<Set<String>>(emptySet())
    val downloadingIds: State<Set<String>> = _downloadingIds
    
    init {
        loadExtensions()
        registerDownloadReceiver()
    }
    
    /**
     * Memuat daftar ekstensi dari repository
     */
    fun loadExtensions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = manager.fetchAvailableExtensions()
                _extensions.value = result
            } catch (e: Exception) {
                _error.value = "Gagal memuat ekstensi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Mengunduh ekstensi
     */
    fun downloadExtension(extension: ExtensionItem) {
        val current = _downloadingIds.value.toMutableSet()
        current.add(extension.pkg)
        _downloadingIds.value = current
        
        manager.downloadExtensionApk(extension)
    }
    
    /**
     * Cek apakah ekstensi terinstal
     */
    fun isInstalled(packageName: String): Boolean {
        return manager.isExtensionInstalled(packageName)
    }
    
    /**
     * Mendapatkan versi terinstal
     */
    fun getInstalledVersion(packageName: String): String? {
        return manager.getInstalledVersion(packageName)
    }
    
    /**
     * Cek apakah perlu update
     */
    fun needsUpdate(extension: ExtensionItem): Boolean {
        if (!isInstalled(extension.pkg)) return false
        
        val installedVersion = getInstalledVersion(extension.pkg) ?: return false
        // Simple string comparison - ideally use versionCode
        return extension.versionName != installedVersion
    }
    
    /**
     * Filter ekstensi berdasarkan tab
     */
    fun getFilteredExtensions(): List<ExtensionItem> {
        return when (_selectedTab.value) {
            ExtensionTab.ALL -> _extensions.value
            ExtensionTab.INSTALLED -> _extensions.value.filter { isInstalled(it.pkg) }
            ExtensionTab.AVAILABLE -> _extensions.value.filter { !isInstalled(it.pkg) }
        }
    }
    
    /**
     * Ganti tab
     */
    fun selectTab(tab: ExtensionTab) {
        _selectedTab.value = tab
    }
    
    /**
     * Register receiver untuk tracking download
     */
    private fun registerDownloadReceiver() {
        downloadReceiver = manager.registerDownloadReceiver { downloadId ->
            viewModelScope.launch {
                val apkFile = manager.getDownloadedApk(downloadId)
                apkFile?.let { file ->
                    // Install APK setelah download selesai
                    manager.installApk(file)
                    // Refresh UI
                    _downloadingIds.value = emptySet()
                }
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        downloadReceiver?.let { manager.unregisterDownloadReceiver(it) }
    }
    
    enum class ExtensionTab {
        ALL, INSTALLED, AVAILABLE
    }
}
