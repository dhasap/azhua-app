# ⛩️ Fase 3: Gerbang Utama Sekte (UI/UX Jetpack Compose)

Fase ini membangun antarmuka pengguna dengan tema Kultivator yang elegan dan gelap.

## 🎨 Tema Kultivator

### Palet Warna - Obsidian & Jade

| Warna | Hex | Penggunaan |
|-------|-----|------------|
| ObsidianBlack | #0B0C10 | Background utama (OLED friendly) |
| DarkSurface | #1F2833 | Card & Bottom Navigation |
| JadeGreen | #45A29E | Primary color (tombol aktif) |
| LightJade | #66FCF1 | Accent & Selection |
| ImperialGold | #D4AF37 | Premium/VIP/Warning |
| TextSilver | #C5C6C7 | Teks sekunder |
| TextWhite | #FFFFFFFF | Teks utama |
| CrimsonRed | #CF6679 | Error/Danger |

### Typography

Menggunakan Material 3 Typography dengan scale:
- Display Large: 57sp (Judul besar)
- Headline Large: 32sp (Judul section)
- Title Large: 22sp (Judul card)
- Body Large: 16sp (Teks utama)
- Label Large: 14sp (Button, chip)

## 🧭 Navigation

### Bottom Navigation Tabs

1. **📚 Pusaka** (Library) - Koleksi Donghua pengguna
2. **🔍 Jelajah** (Discover) - Katalog dan pencarian
3. **⏱️ Riwayat** (History) - History tontonan
4. **📦 Paviliun** (Extensions) - Repository ekstensi

### Screen Sealed Class

```kotlin
sealed class Screen(
    val route: String,
    val title: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
) {
    object Library : Screen("library", "Pusaka", ...)
    object Discover : Screen("discover", "Jelajah", ...)
    object History : Screen("history", "Riwayat", ...)
    object Extensions : Screen("extensions", "Paviliun", ...)
}
```

## 📁 Struktur File

```
app/src/main/java/com/azhua/app/
├── MainActivity.kt              # Entry point dengan AzHuaTheme
├── ui/
│   ├── navigation/
│   │   └── Screen.kt            # Sealed class navigasi
│   ├── screens/
│   │   └── MainScreen.kt        # Bottom Nav + NavHost
│   └── theme/
│       ├── Color.kt             # Palet warna Obsidian & Jade
│       ├── Theme.kt             # AzHuaTheme (dark theme forced)
│       └── Type.kt              # Typography Material 3
```

## ✨ Fitur UI

### 1. Edge-to-Edge Display
```kotlin
enableEdgeToEdge() // Di MainActivity
```

### 2. System Bar Styling
- Status bar: ObsidianBlack
- Navigation bar: DarkSurface
- Icons: Light (untuk dark theme)

### 3. Bottom Navigation
- Selected: Icon hitam di background JadeGreen
- Unselected: Icon silver
- Indicator: JadeGreen rounded

### 4. Screen Transitions
- Enter: Slide from bottom (300ms)
- Exit: Slide to bottom (300ms)

## 🚀 Penggunaan

```kotlin
setContent {
    AzHuaTheme { // Dark theme otomatis
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen() // Bottom Nav + 4 tabs
        }
    }
}
```

## 📸 Preview Expected

Layar utama menampilkan:
1. Background hitam Obsidian yang dalam
2. Bottom Navigation dengan 4 tab
3. Icon tab aktif berwarna Jade dengan background
4. Konten screen yang sedang dipilih
5. Status bar menyatu dengan background

## ✅ Checklist

- [x] Color.kt dengan palet Obsidian & Jade
- [x] Theme.kt dengan dark theme forced
- [x] Type.kt dengan typography lengkap
- [x] Screen.kt sealed class navigasi
- [x] MainScreen.kt dengan Bottom Nav
- [x] Integration ExtensionScreen ke MainScreen
- [x] Edge-to-edge display
- [x] System bar styling

## 🎯 Next Phase

Fase 4: Penempaan Artefak (Membangun Ekstensi Scraper Pertama & DexClassLoader)
