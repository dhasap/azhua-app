# 👁️ Fase 5: Mata Ilahi (Integrasi UI Jelajah & ViewModel)

Fase ini menghubungkan ExtensionLoader dengan Jetpack Compose UI di tab Jelajah (Discover).

## 🔄 Alur Data

```
DiscoverScreen (UI)
    |
    v
DiscoverViewModel
    |
    v
ExtensionLoader.loadInstalledExtensions()
    |
    v
PathClassLoader → Source Instance
    |
    v
source.getPopularAnime() (from APK Ekstensi)
    |
    v
_popularAnime StateFlow
    |
    v
Grid UI dengan Coil Image Loading
```

## 📁 File Baru

### 1. DiscoverViewModel.kt
- Menggunakan `StateFlow` untuk reactive programming
- `loadExtensions()` - Memuat ekstensi saat init
- `selectSource()` - Pilih ekstensi aktif
- `fetchPopularAnime()` - Ambil data dari ekstensi
- `searchAnime()` - Pencarian anime

### 2. DiscoverScreen.kt
- **TopAppBar**: Source selector dropdown + Search + Refresh
- **LazyVerticalGrid**: Grid adaptif 120dp min width
- **AnimeCard**: Cover image + gradient overlay + status badge
- **States**: Empty, Loading, Error, Content

## 🎨 UI Components

### AnimeCard
```
┌─────────────────┐
│  ┌───┐         │  <- Status Badge (ONGOING/END)
│  │IMG│         │  <- Coil AsyncImage
│  └───┘         │
│ ═══════════════│  <- Gradient overlay
│ Title          │  <- Text max 2 lines
│ Year           │
└─────────────────┘
```

### States

| State | Kondisi | UI |
|-------|---------|-----|
| Empty | `activeSources.isEmpty()` | Icon + Text + Button Paviliun |
| Loading | `isLoading = true` | CircularProgressIndicator |
| Error | `error != null` | Error icon + message + retry |
| Content | Anime list ready | LazyVerticalGrid |

## 🎯 Features

### Source Selector
- Dropdown menu di TopAppBar
- Menampilkan semua ekstensi yang aktif
- Auto-select pertama saat load

### Search
- Icon search di TopAppBar
- Expand menjadi TextField
- Memanggil `source.searchAnime()`

### Pull to Refresh
- Icon refresh di TopAppBar
- Memanggil `refreshExtensions()`

## 📦 Dependencies

```kotlin
// Sudah ada dari Fase 3
implementation("io.coil-kt:coil-compose:2.5.0")
```

## 🔗 Integration ke MainScreen

```kotlin
composable(Screen.Discover.route) { 
    DiscoverScreen(
        onAnimeClick = { anime -> ... },
        onNavigateToExtensions = { ... }
    ) 
}
```

## ✅ Checklist

- [x] DiscoverViewModel dengan StateFlow
- [x] DiscoverScreen dengan Grid layout
- [x] AnimeCard dengan Coil image
- [x] Source selector dropdown
- [x] Search functionality
- [x] Empty state dengan navigasi ke Paviliun
- [x] Error state dengan retry
- [x] Loading state
- [x] Integration ke MainScreen

## 🧪 Testing

1. Install ekstensi (`ext-anichin`) ke emulator
2. Buka tab Jelajah
3. Lihat dropdown source selector menampilkan "Anichin"
4. Grid menampilkan 3 anime dummy:
   - Martial Universe Season 4
   - Battle Through The Heavens Season 5
   - Soul Land 2
5. Klik Search → Cari "martial" → Hasil filter
6. Uninstall ekstensi → Empty state muncul

## 🎯 Next Phase

Fase 6: Detail Screen & Player - Menampilkan detail anime dan memutar video
