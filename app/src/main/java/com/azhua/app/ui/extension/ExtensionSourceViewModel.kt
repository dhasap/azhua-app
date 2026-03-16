package com.azhua.app.ui.extension

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.azhua.app.data.managers.ExtensionLoader
import com.azhua.core.contracts.Source
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mengelola ekstensi yang sudah terinstal dan dimuat.
 * 
 * Berbeda dengan ExtensionViewModel yang mengelola repository/download,
 * kelas ini fokus pada loading dan penggunaan ekstensi yang sudah ada.
 */
class ExtensionSourceViewModel(application: Application) : AndroidViewModel(application) {
    
    private val extensionLoader = ExtensionLoader(application)
    
    // State untuk daftar ekstensi yang dimuat
    private val _loadedSources = mutableStateOf<List<Source>>(emptyList())
    val loadedSources: State<List<Source>> = _loadedSources
    
    // State loading
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    // State error
    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error
    
    // Ekstensi yang dipilih/aktif
    private val _selectedSource = mutableStateOf<Source?>(null)
    val selectedSource: State<Source?> = _selectedSource
    
    init {
        loadInstalledExtensions()
    }
    
    /**
     * Memuat semua ekstensi yang terinstal.
     */
    fun loadInstalledExtensions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val sources = extensionLoader.loadInstalledExtensions()
                _loadedSources.value = sources
                
                // Auto-select first source jika ada
                if (_selectedSource.value == null && sources.isNotEmpty()) {
                    _selectedSource.value = sources.first()
                }
                
            } catch (e: Exception) {
                _error.value = "Gagal memuat ekstensi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Pilih ekstensi untuk digunakan.
     */
    fun selectSource(source: Source) {
        _selectedSource.value = source
    }
    
    /**
     * Pilih ekstensi berdasarkan ID.
     */
    fun selectSourceById(sourceId: String) {
        val source = _loadedSources.value.find { it.id == sourceId }
        if (source != null) {
            _selectedSource.value = source
        }
    }
    
    /**
     * Reload semua ekstensi.
     */
    fun reloadExtensions() {
        loadInstalledExtensions()
    }
    
    /**
     * Cek apakah ada ekstensi terinstal.
     */
    fun hasExtensions(): Boolean {
        return _loadedSources.value.isNotEmpty()
    }
    
    /**
     * Dapatkan jumlah ekstensi terinstal.
     */
    fun getExtensionCount(): Int {
        return _loadedSources.value.size
    }
    
    /**
     * Dapatkan daftar bahasa unik dari semua ekstensi.
     */
    fun getAvailableLanguages(): List<String> {
        return _loadedSources.value.map { it.language }.distinct()
    }
    
    /**
     * Filter ekstensi berdasarkan bahasa.
     */
    fun getSourcesByLanguage(lang: String): List<Source> {
        return _loadedSources.value.filter { it.language.equals(lang, ignoreCase = true) }
    }
}
