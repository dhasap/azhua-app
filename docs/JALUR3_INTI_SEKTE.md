# 🏛️ Jalur 3: Menyempurnakan Inti Sekte (Room Database)

## Ringkasan

Jalur ini membangun sistem memori permanen untuk AzHua menggunakan Room Database. Riwayat tontonan pengguna akan tersimpan di SQLite dan bisa diakses kembali.

## 🏗️ Arsitektur Database

```
app/src/main/java/com/azhua/app/data/local/
├── WatchHistory.kt       # Entity (Batu Prasasti)
├── HistoryDao.kt         # DAO (Array Mantra)
└── AppDatabase.kt        # Database (Kuil Inti)
```

## 📦 Dependencies

```kotlin
// Plugin KSP
dependencies {
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

// Room
dependencies {
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
}
```

## 🗿 Entity: WatchHistory

```kotlin
@Entity(tableName = "watch_history")
data class WatchHistory(
    @PrimaryKey
    val animeUrl: String,      // ID unik
    val title: String,
    val coverUrl: String,
    val sourceName: String,    // Nama ekstensi
    val episodeUrl: String,    // Episode terakhir
    val episodeName: String,
    val timestampMs: Long,     // Progress (ms)
    val durationMs: Long,      // Total durasi
    val lastWatchedAt: Long    // Timestamp
)
```

### Helper Properties
- `progressPercent: Int` - Persentase progress
- `formattedTimestamp: String` - Format mm:ss
- `formattedDuration: String` - Format mm:ss
- `isCompleted: Boolean` - Sudah > 90%?

## 📜 DAO: HistoryDao

| Mantra | Fungsi |
|--------|--------|
| `insertOrUpdate()` | Simpan/update riwayat |
| `getAllHistory()` | Ambil semua (Flow) |
| `getHistoryByAnime()` | Ambil satu anime |
| `deleteHistory()` | Hapus satu |
| `clearAllHistory()` | Hapus semua |
| `searchHistory()` | Cari berdasarkan judul |

## 🏛️ AppDatabase

```kotlin
@Database(entities = [WatchHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    
    companion object {
        fun getDatabase(context: Context): AppDatabase
    }
}
```

## 💾 Menyimpan Riwayat (PlayerScreen)

```kotlin
DisposableEffect(Unit) {
    onDispose {
        val currentPos = exoPlayer.currentPosition
        val totalDur = exoPlayer.duration
        
        // Jangan simpan jika < 2 detik
        if (currentPos > 2000L) {
            coroutineScope.launch(Dispatchers.IO) {
                historyDao.insertOrUpdate(
                    WatchHistory(
                        animeUrl = animeUrl,
                        title = animeTitle,
                        coverUrl = animeCover,
                        sourceName = source.name,
                        episodeUrl = episode.sourceUrl,
                        episodeName = episode.title,
                        timestampMs = currentPos,
                        durationMs = totalDur
                    )
                )
            }
        }
        exoPlayer.release()
    }
}
```

## 📱 UI: HistoryScreen

### Features
- List riwayat dengan cover image
- Progress bar per item
- Search functionality
- Swipe to delete (IconButton)
- Clear all history
- Empty state

### HistoryItem Layout
```
┌─────────────────────────────────────┐
│ ┌───┐  Title                 [🗑️] │
│ │IMG│  Episode 1 • Anichin         │
│ └───┘  ▓▓▓▓▓░░░░░ 14:20 / 24:00   │
└─────────────────────────────────────┘
```

## 🔄 Alur Data

```
User nonton video
    ↓
PlayerScreen (ExoPlayer)
    ↓
onDispose → Simpan ke Room
    ↓
WatchHistory Entity
    ↓
SQLite Database (azhua_database)
    ↓
HistoryScreen (Flow)
    ↓
UI Riwayat Tontonan
```

## ✅ Checklist

- [x] Plugin KSP
- [x] Room dependencies
- [x] WatchHistory Entity
- [x] HistoryDao
- [x] AppDatabase (Singleton)
- [x] PlayerScreen: simpan saat dispose
- [x] HistoryScreen UI
- [x] HistoryViewModel
- [x] Integrasi ke MainScreen

## 🧪 Testing

1. Build aplikasi
2. Install ekstensi dan core app
3. Nonton video > 5 detik
4. Back → Riwayat harus muncul
5. Progress bar sesuai
6. Klik riwayat → Buka detail
7. Clear all → List kosong

## 🎯 Next: Jalur Lainnya

| Jalur | Fokus |
|-------|-------|
| 2 | GitHub Repository (Paviliun Awan) |
| 4 | Release & Production |

**Inti Sekte telah kuat! Data pengguna aman tersimpan!** 🏛️✨
