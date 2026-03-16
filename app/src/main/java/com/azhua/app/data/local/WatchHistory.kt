package com.azhua.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity WatchHistory - Batu Prasasti Riwayat Tontonan
 *
 * Menyimpan data riwayat tontonan pengguna di database Room.
 * Setiap anime yang pernah ditonton akan tersimpan dengan progress terakhir.
 */
@Entity(tableName = "watch_history")
data class WatchHistory(
    @PrimaryKey
    val animeUrl: String, // URL anime sebagai ID unik
    
    val title: String,
    
    val coverUrl: String,
    
    val sourceName: String, // Nama ekstensi (misal: "Anichin")
    
    val episodeUrl: String, // URL episode terakhir yang ditonton
    
    val episodeName: String, // Nama episode
    
    val timestampMs: Long, // Posisi terakhir ditonton (dalam milidetik)
    
    val durationMs: Long,  // Total durasi video
    
    val lastWatchedAt: Long = System.currentTimeMillis() // Waktu terakhir kali dibuka
) {
    /**
     * Format progress sebagai persentase
     */
    val progressPercent: Int
        get() = if (durationMs > 0) {
            ((timestampMs * 100) / durationMs).toInt()
        } else 0
    
    /**
     * Format timestamp sebagai mm:ss
     */
    val formattedTimestamp: String
        get() = formatDuration(timestampMs)
    
    /**
     * Format durasi sebagai mm:ss
     */
    val formattedDuration: String
        get() = formatDuration(durationMs)
    
    /**
     * Cek apakah sudah selesai ditonton (> 90%)
     */
    val isCompleted: Boolean
        get() = progressPercent >= 90
    
    companion object {
        fun formatDuration(millis: Long): String {
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}
