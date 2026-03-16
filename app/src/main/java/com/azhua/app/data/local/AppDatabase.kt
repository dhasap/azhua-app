package com.azhua.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase - Kuil Inti Database
 *
 * Database utama aplikasi AzHua menggunakan Room.
 * Menyediakan akses ke semua DAO (Data Access Object).
 */
@Database(
    entities = [WatchHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Akses DAO untuk riwayat tontonan.
     */
    abstract fun historyDao(): HistoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Mendapatkan instance database (Singleton pattern).
         * Thread-safe menggunakan synchronized.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "azhua_database" // Nama file database
                )
                .fallbackToDestructiveMigration() // Hapus data jika versi berubah (dev only!)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
