# 🎬 Fase 6: Aula Rincian & Cermin Ilahi (Detail & Player)

Fase ini menyelesaikan pengalaman streaming dengan Detail Screen dan Video Player menggunakan ExoPlayer.

## 📦 Dependencies Baru

```kotlin
// Media3 (ExoPlayer) untuk memutar video
val media3Version = "1.3.0"
implementation("androidx.media3:media3-exoplayer:$media3Version")
implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
implementation("androidx.media3:media3-ui:$media3Version")
```

## 🎨 UI Components

### 1. DetailScreen
**Layout:**
```
┌─────────────────────────────────┐
│ ← Detail Donghua     [Refresh] │  <- TopAppBar
├─────────────────────────────────┤
│ ┌─────┐  Title                  │
│ │Cover│  Alt Title              │  <- HeaderSection
│ │IMG  │  ⭐ Rating               │
│ │     │  📅 Year | Tipe         │
│ └─────┘  [Genre] [Genre]        │
├─────────────────────────────────┤
│ Sinopsis                        │  <- DescriptionSection
│ Long text description...        │
├─────────────────────────────────┤
│ Daftar Episode (12)    [⟳]     │
├─────────────────────────────────┤
│ ┌──┬──────────────────────┬──┐ │
│ │01│ Episode 1            │▶️│ │  <- EpisodeItem
│ └──┴──────────────────────┴──┘ │
│ ┌──┬──────────────────────┬──┐ │
│ │02│ Episode 2            │▶️│ │
│ └──┴──────────────────────┴──┘ │
└─────────────────────────────────┘
```

**Features:**
- Cover image dengan status badge (ONGOING/COMPLETED)
- Meta info: Rating, Year, Type, Language
- Genre chips
- Sinopsis expandable
- Episode list dengan nomor, judul, durasi
- Pull to refresh

### 2. PlayerScreen
**Layout (Fullscreen):**
```
┌─────────────────────────────────────────┐
│ [←]                              [Full] │  <- Overlay controls
│                                         │
│                                         │
│              ┌─────────┐                │
│              │ ExoPlayer│               │  <- PlayerView
│              │  View   │                │
│              │ (16:9)  │                │
│              └─────────┘                │
│                                         │
│           [ Circular                    │
│             Progress ]                  │  <- Loading
│                                         │
│           "Memuat video..."             │
└─────────────────────────────────────────┘
```

**Features:**
- Fullscreen landscape mode
- ExoPlayer dengan controller default
- HLS (.m3u8) support
- Multiple quality selection (backend ready)
- Error handling dengan retry
- Loading overlay
- Auto fullscreen on enter
- Lifecycle aware (pause on background)

## 🔄 Navigation Flow

```
DiscoverScreen (Grid)
    |
    | onAnimeClick(anime)
    |→ navController.navigate("detail/{encodedUrl}")
    v
DetailScreen (Episode List)
    |
    | onEpisodeClick(episode)
    |→ navController.navigate("player/{encodedUrl}")
    v
PlayerScreen (Fullscreen Video)
    |
    | onBackClick()
    |→ navController.popBackStack()
    v
Back to DetailScreen
```

## 🎯 URL Encoding

Karena URL anime mengandung `/`, kita wajib encode:

```kotlin
// Navigate to Detail
val encodedUrl = URLEncoder.encode(anime.sourceUrl, StandardCharsets.UTF_8.toString())
navController.navigate("detail/$encodedUrl")

// Decode in destination
val encodedUrl = backStackEntry.arguments?.getString("encodedUrl") ?: ""
val url = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
```

## 🎬 ExoPlayer Setup

```kotlin
// 1. Create player
val exoPlayer = ExoPlayer.Builder(context).build()

// 2. Create media item
val mediaItem = MediaItem.fromUri(Uri.parse(streamUrl))

// 3. Set and prepare
exoPlayer.setMediaItem(mediaItem)
exoPlayer.prepare()
exoPlayer.playWhenReady = true

// 4. Bind to PlayerView
PlayerView(context).apply {
    player = exoPlayer
}

// 5. Cleanup
DisposableEffect(Unit) {
    onDispose { exoPlayer.release() }
}
```

## 🚀 Alur Kerja Player

1. User klik episode → Navigate ke PlayerScreen dengan episode URL
2. PlayerScreen memanggil `source.getVideoStreams(episodeId)`
3. Ekstensi mengembalikan list VideoStream (berbagai quality)
4. Pilih stream terbaik (prioritas: FHD > HD > SD)
5. Load ke ExoPlayer
6. Tampilkan loading indicator saat buffering
7. Video mulai diputar!

## ✅ Checklist

- [x] Media3 dependencies
- [x] DetailViewModel
- [x] DetailScreen (Header, Description, EpisodeList)
- [x] PlayerScreen (ExoPlayer, Fullscreen)
- [x] Navigation routes (Discover → Detail → Player)
- [x] URL encoding/decoding
- [x] Error handling
- [x] Loading states
- [x] Lifecycle management

## 🧪 Testing

1. Install ekstensi (`ext-anichin`)
2. Buka tab Jelajah → Klik anime
3. DetailScreen muncul dengan:
   - Cover image
   - Title & meta info
   - List episode
4. Klik episode → PlayerScreen fullscreen
5. Video test HLS berputar!
6. Back button kembali ke Detail

## 🎯 Project Complete!

AzHua sekarang memiliki:
- ✅ Modular architecture
- ✅ Extension system dengan PathClassLoader
- ✅ Repository & Download manager
- ✅ Dark theme UI (Obsidian & Jade)
- ✅ Discover grid dengan data dari ekstensi
- ✅ Detail screen dengan episode list
- ✅ Video player dengan ExoPlayer

**Selamat! AzHua v1.0 telah lahir! 🎉**
