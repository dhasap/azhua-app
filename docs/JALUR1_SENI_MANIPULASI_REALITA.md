# 🗡️ Jalur 1: Seni Manipulasi Realita (Real Web Scraping)

## Ringkasan

Jalur ini mengubah ekstensi Anichin dari dummy data menjadi scraper nyata yang bisa mengekstrak data dari `anichin.co.id`.

## 🏗️ Arsitektur Baru

```
ext-anichin/
├── network/
│   └── AnichinClient.kt          # HTTP Client dengan User-Agent spoofing
├── parser/
│   ├── AnimeListParser.kt        # Parser daftar anime
│   ├── AnimeDetailParser.kt      # Parser detail anime
│   ├── EpisodeParser.kt          # Parser daftar episode
│   └── VideoStreamParser.kt      # Parser video streams (.m3u8)
└── AnichinSource.kt              # Implementasi Source interface
```

## 🛠️ Teknik yang Diterapkan

### 1. HTTP Client (AnichinClient)
- User-Agent spoofing (Chrome 120)
- Timeout handling (30 detik)
- Redirect following
- Header lengkap (Accept, Accept-Language, dll)
- Method: GET dan POST

### 2. HTML DOM Parsing
- Jsoup selectors untuk ekstrak data
- Pattern matching untuk elemen yang dinamis
- Fallback selectors untuk kompatibilitas

### 3. Video Stream Extraction
- Regex pattern untuk URL `.m3u8`
- Iframe extraction (StreamWish, Dood, dll)
- Base64 decoding untuk link tersembunyi
- JavaScript variable parsing
- Multi-server support (Server 1, 2, 3, ...)

## 📋 Parser Details

### AnimeListParser
```kotlin
// Selector: div.bsx, div.utao, article.item
// Extract: title, coverUrl, status, type, rating, year
```

### AnimeDetailParser
```kotlin
// Extract: title, altTitles, description, cover, banner
// Parse: meta info (status, type, rating, year, genres)
```

### EpisodeParser
```kotlin
// Selector: .eplister li, .episodelist li
// Extract: number, title, thumbnail, duration
```

### VideoStreamParser
```kotlin
// Method 1: Direct m3u8 regex
// Method 2: Iframe extraction
// Method 3: Base64 decoding
// Method 4: Script tag parsing
```

## 🔗 Endpoint yang Di-scrape

| Function | URL Pattern |
|----------|-------------|
| getPopularAnime | `/` atau `/page/{n}/` |
| getLatestAnime | `/anime/?status=ongoing&order=update` |
| getUpcomingAnime | `/anime/?status=upcoming` |
| getCompletedAnime | `/anime/?status=completed` |
| searchAnime | `/?s={query}` (POST) |
| getAnimeDetails | `/{anime-slug}/` |
| getEpisodes | `/{anime-slug}/` |
| getVideoStreams | `/episode/{episode-slug}/` |
| getAnimeByGenre | `/genre/{genre}/` |

## ⚔️ Teknik Anti-Deteksi

### User-Agent Spoofing
```kotlin
const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36..."
```

### Headers Lengkap
```kotlin
header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
header("Accept-Language", "en-US,en;q=0.5")
header("Accept-Encoding", "gzip, deflate, br")
```

## 🎬 Ekstraksi Video (Paling Dalam!)

### Pattern Regex
```kotlin
// m3u8 URL
val M3U8_PATTERN = Pattern.compile("(https?://[^\\s\"'<>]+\\.m3u8[^\\s\"'<>]*)")

// Base64 data
val BASE64_PATTERN = Pattern.compile("data:text/html;base64,([A-Za-z0-9+/=]+)")
```

### Alur Ekstraksi
1. Cari link m3u8 langsung di HTML
2. Jika tidak, buka iframe player
3. Cek data base64 tersembunyi
4. Parse JavaScript variables
5. Coba server alternatif

## 🧪 Testing

### Build Ekstensi
```bash
./gradlew :ext-anichin:assembleDebug
```

### Install ke Emulator
```bash
adb install ext-anichin/build/outputs/apk/debug/ext-anichin-debug.apk
```

### Verifikasi
1. Buka AzHua → Tab Jelajah
2. Pastikan source "Anichin" terdeteksi
3. Grid menampilkan anime dari web (bukan dummy!)
4. Klik anime → Detail berisi data real
5. Klik episode → Video bisa diputar!

## ⚠️ Catatan Penting

### Rate Limiting
- Jangan scrape terlalu agresif
- Delay antar request jika perlu
- Respect robots.txt

### Website Changes
- Struktur HTML bisa berubah
- Update selectors jika scraping gagal
- Monitor log untuk error

### Legal Consideration
- Hanya untuk pembelajaran
- Respect copyright
- Gunakan dengan bijak

## ✅ Checklist

- [x] AnichinClient dengan HTTP headers
- [x] AnimeListParser untuk popular/latest
- [x] AnimeDetailParser untuk detail page
- [x] EpisodeParser untuk list episode
- [x] VideoStreamParser untuk .m3u8 extraction
- [x] Updated AnichinSource dengan parser
- [x] OkHttp dependency
- [x] Version bump ke 2.0.0

## 🎯 Next: Jalur 2 (Paviliun Awan)

Setelah scraping berfungsi, kita bisa:
- Setup repository GitHub
- Upload APK ekstensi
- Distribusi via Paviliun Kitab

**Artefak Anichin sekarang memiliki Jiwa Nyata!** ⚔️✨
