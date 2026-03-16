# 🔥 Fase 4: Penempaan Artefak (Ekstensi & Class Loader)

Fase ini membangun ekstensi pertama (Anichin) dan sistem PathClassLoader untuk memuat kode dari APK terpisah.

## 📦 Struktur Modul Baru

```
AzHua/
├── app/                         # Core App (sudah ada)
├── core-contracts/             # Interface contracts (sudah ada)
└── ext-anichin/                # 🆕 Ekstensi pertama (APK terpisah)
```

## 🏗️ ext-anichin - Ekstensi Scraper

### 1. Konfigurasi Gradle

```kotlin
plugins {
    id("com.android.application")  // PENTING: application, bukan library
}

dependencies {
    // compileOnly: core-contracts sudah ada di Core App
    compileOnly(project(":core-contracts"))
    
    // Jsoup untuk web scraping
    implementation("org.jsoup:jsoup:1.17.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

**Catatan Penting**: Gunakan `compileOnly` untuk `core-contracts` agar interface tidak duplicate di APK ekstensi.

### 2. AndroidManifest.xml

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    
    <application>
        <!-- SEGEL: Identifikasi kelas utama -->
        <meta-data
            android:name="azhua.extension.class"
            android:value="com.azhua.ext.anichin.AnichinSource" />
    </application>
</manifest>
```

### 3. AnichinSource.kt

Implementasi `Source` interface dengan:
- `id`, `name`, `baseUrl`, `version`, `language`
- `getPopularAnime()` - Scraping anime popular
- `searchAnime()` - Pencarian anime
- `getAnimeDetails()` - Detail anime + episodes
- `getVideoStreams()` - Link streaming video

**Status**: Dummy data untuk testing struktur. TODO: Implementasi Jsoup sebenarnya.

## ⚔️ ExtensionLoader - PathClassLoader Master

### Alur Kerja

```
Core App
    |
    v
loadInstalledExtensions()
    |
    v
Scan semua APK terinstal
    |
    v
Cek META-DATA "azhua.extension.class"
    |
    v
createPackageContext(pkgName, IGNORE_SECURITY | INCLUDE_CODE)
    |
    v
PathClassLoader(apkPath, nativeLibPath, parentClassLoader)
    |
    v
Class.forName(className, false, classLoader)
    |
    v
instance.newInstance() as Source
    |
    v
✅ Source instance siap digunakan!
```

### Key Code

```kotlin
// 1. Context dari APK ekstensi
val extContext = context.createPackageContext(
    packageName,
    Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
)

// 2. PathClassLoader
val classLoader = PathClassLoader(
    extContext.applicationInfo.sourceDir,
    extContext.applicationInfo.nativeLibraryDir,
    context.classLoader  // Parent: Core App classloader
)

// 3. Load class
val sourceClass = Class.forName(className, false, classLoader)
val sourceInstance = sourceClass.newInstance() as Source
```

## 🔄 ExtensionSourceViewModel

ViewModel untuk mengelola ekstensi yang sudah dimuat:
- `loadInstalledExtensions()` - Scan & load semua ekstensi
- `selectSource()` - Pilih ekstensi aktif
- `reloadExtensions()` - Refresh daftar ekstensi

## ✅ Checklist Fase 4

- [x] Modul `ext-anichin` dibuat
- [x] `build.gradle.kts` dengan `compileOnly(project(":core-contracts"))`
- [x] `AndroidManifest.xml` dengan meta-data
- [x] `AnichinSource.kt` implementasi Source interface
- [x] `ExtensionLoader.kt` dengan PathClassLoader
- [x] `ExtensionSourceViewModel.kt` untuk state management

## 🧪 Testing

1. Build APK ekstensi: `./gradlew :ext-anichin:assembleDebug`
2. Install APK ekstensi ke emulator
3. Jalankan Core App
4. Cek Logcat untuk pesan "Berhasil memuat ekstensi: Anichin"

## 🎯 Next Phase

Fase 5: Integrasi UI dengan ekstensi yang dimuat - membuat layar Discover yang menggunakan Source dari ekstensi.
