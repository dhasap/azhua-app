package com.azhua.app.ui.screens.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azhua.core.contracts.Source
import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.Episode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * DetailViewModel - Aula Rincian
 *
 * ViewModel untuk mengelola data layar detail anime.
 * Mengambil informasi lengkap anime dan daftar episode dari ekstensi.
 */
class DetailViewModel : ViewModel() {

    companion object {
        private const val TAG = "DetailViewModel"
    }

    // State untuk detail anime
    private val _animeDetail = MutableStateFlow<Anime?>(null)
    val animeDetail: StateFlow<Anime?> = _animeDetail.asStateFlow()

    // State untuk daftar episode
    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes: StateFlow<List<Episode>> = _episodes.asStateFlow()

    // State loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Memuat detail anime dan daftar episode dari ekstensi.
     *
     * @param source Ekstensi yang sedang aktif
     * @param url URL anime yang akan dimuat detailnya
     * @param initialAnime Data anime awal (dari list, untuk tampil sementara)
     */
    fun loadAnimeDetails(source: Source, url: String, initialAnime: Anime) {
        // Tampilkan data awal dulu (cover & title) agar UI tidak kosong
        _animeDetail.value = initialAnime
        _error.value = null

        viewModelScope.launch {
            _isLoading.value = true
            Log.d(TAG, "Memuat detail anime dari ${source.name}: $url")

            try {
                // Memanggil getAnimeDetails & getEpisodes dari Ekstensi! 🔥
                val detail = source.getAnimeDetails(url)
                val eps = source.getEpisodes(url)

                _animeDetail.value = detail
                _episodes.value = eps

                Log.d(TAG, "Berhasil memuat ${eps.size} episode")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal memuat detail anime", e)
                _error.value = "Gagal memuat data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refresh data dari ekstensi.
     */
    fun refresh(source: Source, url: String, initialAnime: Anime) {
        loadAnimeDetails(source, url, initialAnime)
    }

    /**
     * Mendapatkan episode berdasarkan nomor.
     */
    fun getEpisodeByNumber(number: Int): Episode? {
        return _episodes.value.find { it.number == number }
    }

    /**
     * Mendapatkan episode berikutnya dari episode saat ini.
     */
    fun getNextEpisode(currentEpisode: Episode): Episode? {
        return _episodes.value.find { it.number == currentEpisode.number + 1 }
    }

    /**
     * Mendapatkan episode sebelumnya dari episode saat ini.
     */
    fun getPreviousEpisode(currentEpisode: Episode): Episode? {
        return _episodes.value.find { it.number == currentEpisode.number - 1 }
    }
}
