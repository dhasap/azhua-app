# 🏛️ Fase 2: Paviliun Kitab (Sistem Repositori Ekstensi)

Fase ini membangun "App Store" mini di dalam aplikasi AzHua untuk mengelola ekstensi.

## 📋 Fitur Utama

### 1. Repository JSON
- File `index.json` di GitHub sebagai katalog ekstensi
- Format: Array of ExtensionItem dengan metadata lengkap

### 2. ExtensionManager
Kelas utama yang menangani:
- `fetchAvailableExtensions()` - Mengambil daftar dari GitHub
- `downloadExtensionApk()` - Download APK via DownloadManager
- `installApk()` - Trigger system package installer
- `isExtensionInstalled()` - Cek status instalasi

### 3. ExtensionScreen (UI)
Tampilan Jetpack Compose dengan:
- **3 Tab**: Semua, Terinstal, Tersedia
- **ExtensionCard**: Menampilkan icon, nama, versi, bahasa
- **Download Button**: Dengan progress indicator
- **Update Indicator**: Badge untuk versi baru
- **Empty State**: Pesan saat tidak ada data

### 4. Security & Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

### 5. FileProvider
Wajib untuk Android 7+ (API 24) agar bisa install APK.

## 🚀 Cara Penggunaan

### Setup Repository GitHub

1. Buat repository publik `azhua-extensions`
2. Buat file `index.json`:
```json
[
  {
    "name": "Anichin",
    "pkg": "com.azhua.ext.anichin",
    "versionCode": 1,
    "versionName": "1.0.0",
    "lang": "id",
    "icon": "https://raw.githubusercontent.com/USER/azhua-extensions/main/icons/anichin.png",
    "apkUrl": "https://github.com/USER/azhua-extensions/releases/download/v1.0.0/anichin-v1.0.0.apk"
  }
]
```

3. Ganti `YOUR_GITHUB_NAME` di `ExtensionManager.kt` dengan username GitHub Anda.

### Alur Kerja

```
User -> ExtensionScreen -> ExtensionViewModel -> ExtensionManager
                                          |
                                          v
                                     GitHub RAW (index.json)
                                          |
                                          v
                              DownloadManager -> APK File
                                          |
                                          v
                              FileProvider -> Package Installer
```

## 📂 Struktur File

```
app/src/main/
├── AndroidManifest.xml          # Permissions + FileProvider
├── java/com/azhua/app/
│   ├── MainActivity.kt          # Entry point
│   └── data/
│       ├── models/
│       │   └── ExtensionItem.kt # Data class
│       └── managers/
│           └── ExtensionManager.kt  # Core logic
│   └── ui/
│       └── extension/
│           ├── ExtensionViewModel.kt
│           └── ExtensionScreen.kt
└── res/xml/
    └── provider_paths.xml       # FileProvider config
```

## 🔧 Dependencies

```kotlin
// OkHttp untuk HTTP Requests
implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Gson untuk parsing JSON
implementation("com.google.code.gson:gson:2.10.1")

// Coil untuk loading gambar
implementation("io.coil-kt:coil-compose:2.5.0")
```

## ✅ Checklist

- [x] Permission INTERNET
- [x] Permission REQUEST_INSTALL_PACKAGES
- [x] FileProvider setup
- [x] ExtensionItem model
- [x] ExtensionManager
- [x] ExtensionViewModel
- [x] ExtensionScreen UI
- [x] Download tracking
- [x] Install APK handler
- [x] Tab filtering

## 🎯 Next Phase

Fase 3: Gerbang Utama Sekte (UI/UX Tema Kultivator dengan Jetpack Compose)
