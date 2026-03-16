package com.azhua.app.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.azhua.app.data.local.AppDatabase
import com.azhua.app.data.local.WatchHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * HistoryViewModel - Pengelola Prasasti
 *
 * ViewModel untuk mengelola riwayat tontonan dari Room Database.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val historyDao = AppDatabase.getDatabase(application).historyDao()

    // State untuk daftar riwayat
    private val _historyList = MutableStateFlow<List<WatchHistory>>(emptyList())
    val historyList: StateFlow<List<WatchHistory>> = _historyList.asStateFlow()

    // State loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State untuk query pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadHistory()
    }

    /**
     * Memuat semua riwayat tontonan.
     */
    private fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                historyDao.getAllHistory().collect { list ->
                    _historyList.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    /**
     * Menghapus satu riwayat.
     */
    fun deleteHistory(animeUrl: String) {
        viewModelScope.launch {
            historyDao.deleteHistory(animeUrl)
        }
    }

    /**
     * Menghapus semua riwayat.
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            historyDao.clearAllHistory()
        }
    }

    /**
     * Update query pencarian.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Mendapatkan jumlah riwayat.
     */
    fun getHistoryCount(): Int {
        return _historyList.value.size
    }

    /**
     * Cek apakah ada riwayat.
     */
    fun hasHistory(): Boolean {
        return _historyList.value.isNotEmpty()
    }
}
