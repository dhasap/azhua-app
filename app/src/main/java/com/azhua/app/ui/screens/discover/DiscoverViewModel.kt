package com.azhua.app.ui.screens.discover

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.azhua.app.data.managers.ExtensionLoader
import com.azhua.core.contracts.Source
import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.AnimeStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * DiscoverViewModel - Mata Ilahi
 * 
 * ViewModel untuk mengelola data tab Jelajah (Discover).
 * Mengambil data dari ekstensi yang aktif menggunakan ExtensionLoader
 * dan menampilkannya dalam bentuk Grid.
 */
class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "DiscoverViewModel"
    }

    private val extensionLoader = ExtensionLoader(application)

    // State untuk menyimpan daftar ekstensi yang berhasil dimuat
    private val _activeSources = MutableStateFlow<List<Source>>(emptyList())
    val activeSources: StateFlow<List<Source>> = _activeSources.asStateFlow()

    // State untuk sumber yang sedang dipilih (misal: Anichin)
    private val _selectedSource = MutableStateFlow<Source?>(null)
    val selectedSource: StateFlow<Source?> = _selectedSource.asStateFlow()

    // State untuk daftar Donghua populer
    private val _popularAnime = MutableStateFlow<List<Anime>>(emptyList())
    val popularAnime: StateFlow<List<Anime>> = _popularAnime.asStateFlow()

    // State untuk daftar anime terbaru
    private val _latestAnime = MutableStateFlow<List<Anime>>(emptyList())
    val latestAnime: StateFlow<List<Anime>> = _latestAnime.asStateFlow()

    // State loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadExtensions()
    }

    /**
     * Memuat semua ekstensi yang terinstal.
     */
    private fun loadExtensions() {
        Log.d(TAG, "Memuat ekstensi...")
        val sources = extensionLoader.loadInstalledExtensions()
        _activeSources.value = sources
        Log.d(TAG, "Berhasil memuat ${sources.size} ekstensi")

        // Auto-pilih ekstensi pertama jika ada
        if (sources.isNotEmpty()) {
            selectSource(sources.first())
        } else {
            _error.value = "Tidak ada ekstensi yang terinstal. Silakan kunjungi Paviliun."
        }
    }

    /**
     * Memilih ekstensi aktif.
     */
    fun selectSource(source: Source) {
        Log.d(TAG, "Memilih sumber: ${source.name} (${source.language})")
        _selectedSource.value = source
        _error.value = null
        fetchPopularAnime(source)
        fetchLatestAnime(source)
    }

    /**
     * Memuat ulang semua ekstensi.
     */
    fun refreshExtensions() {
        loadExtensions()
    }

    /**
     * Mengambil daftar anime populer dari ekstensi.
     */
    private fun fetchPopularAnime(source: Source) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Mengambil anime populer dari ${source.name}...")
                // Memanggil method GET POPULAR dari APK Ekstensi! 🔥
                val animeList = source.getPopularAnime(page = 1)
                _popularAnime.value = animeList
                Log.d(TAG, "Berhasil mendapat ${animeList.size} anime populer")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mengambil anime populer", e)
                _error.value = "Gagal memuat data: ${e.message}"
                _popularAnime.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mengambil daftar anime terbaru dari ekstensi.
     */
    private fun fetchLatestAnime(source: Source) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Mengambil anime terbaru dari ${source.name}...")
                val animeList = source.getLatestAnime(page = 1)
                _latestAnime.value = animeList
                Log.d(TAG, "Berhasil mendapat ${animeList.size} anime terbaru")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mengambil anime terbaru", e)
                _latestAnime.value = emptyList()
            }
        }
    }

    /**
     * Mencari anime.
     */
    fun searchAnime(query: String) {
        val source = _selectedSource.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d(TAG, "Mencari '$query' di ${source.name}...")
                val results = source.searchAnime(query = query, page = 1)
                _popularAnime.value = results
                Log.d(TAG, "Berhasil mendapat ${results.size} hasil pencarian")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mencari anime", e)
                _error.value = "Gagal mencari: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Mendapatkan jumlah ekstensi aktif.
     */
    fun getExtensionCount(): Int {
        return _activeSources.value.size
    }

    /**
     * Mendapatkan daftar bahasa dari ekstensi.
     */
    fun getAvailableLanguages(): List<String> {
        return _activeSources.value.map { it.language }.distinct()
    }
}

/**
 * Data class untuk UI state.
 */
data class DiscoverUiState(
    val sources: List<Source> = emptyList(),
    val selectedSource: Source? = null,
    val popularAnime: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
