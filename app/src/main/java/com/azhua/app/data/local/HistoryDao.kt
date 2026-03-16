package com.azhua.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * HistoryDao - Array Mantra Query
 *
 * DAO (Data Access Object) untuk mengakses tabel watch_history.
 * Berisi mantra SQL untuk membaca dan menulis data riwayat tontonan.
 */
@Dao
interface HistoryDao {
    
    /**
     * Menyimpan atau memperbarui riwayat.
     * Jika anime yang sama sudah ada, akan di-update dengan data terbaru.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(history: WatchHistory)
    
    /**
     * Mengambil semua riwayat secara reaktif (Flow).
     * Diurutkan dari yang terbaru ditonton.
     */
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedAt DESC")
    fun getAllHistory(): Flow<List<WatchHistory>>
    
    /**
     * Mengambil riwayat tontonan spesifik untuk satu anime.
     * Berguna untuk menampilkan progress di DetailScreen.
     */
    @Query("SELECT * FROM watch_history WHERE animeUrl = :url LIMIT 1")
    suspend fun getHistoryByAnime(url: String): WatchHistory?
    
    /**
     * Menghapus satu riwayat berdasarkan URL anime.
     */
    @Query("DELETE FROM watch_history WHERE animeUrl = :url")
    suspend fun deleteHistory(url: String)
    
    /**
     * Menghapus semua riwayat tontonan.
     * Gunakan dengan hati-hati!
     */
    @Query("DELETE FROM watch_history")
    suspend fun clearAllHistory()
    
    /**
     * Mengambil jumlah total riwayat.
     */
    @Query("SELECT COUNT(*) FROM watch_history")
    suspend fun getHistoryCount(): Int
    
    /**
     * Mencari riwayat berdasarkan judul.
     */
    @Query("SELECT * FROM watch_history WHERE title LIKE '%' || :query || '%' ORDER BY lastWatchedAt DESC")
    fun searchHistory(query: String): Flow<List<WatchHistory>>
}
