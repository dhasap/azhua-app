# 🛡️ Jalur 4: Kesengsaraan Surgawi (Release & Production)

## Ringkasan

Jalur terakhir ini mempersiapkan AzHua untuk distribusi ke pengguna sungguhan dengan:
- Keystore & App Signing
- ProGuard/R8 Obfuscation
- APK Release yang teroptimasi

## 🔐 1. Keystore (Segel Jiwa)

### File Keystore
- **Lokasi**: `keystore/azhua_keystore.jks`
- **Alias**: `azhua_key`
- **Password**: `azhua2026` (ganti di production!)
- **Valid**: 10.000 hari (~27 tahun)

### ⚠️ PERINGATAN KRITIS
**JANGAN PERNAH HILANGKAN FILE KEYSTORE INI!**  
File ini adalah identitas digital aplikasi Anda. Jika hilang, Anda tidak bisa update aplikasi di Play Store.

### Command Pembuatan
```bash
keytool -genkey -v \
  -keystore azhua_keystore.jks \
  -keyalg RSA -keysize 2048 \
  -validity 10000 \
  -alias azhua_key
```

## 🛡️ 2. Konfigurasi Signing (build.gradle.kts)

### Core App (app/build.gradle.kts)
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/azhua_keystore.jks")
            storePassword = "azhua2026"
            keyAlias = "azhua_key"
            keyPassword = "azhua2026"
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

## 📜 3. ProGuard Rules (Mantra Pelindung)

### app/proguard-rules.pro
```proguard
# --- LINDUNGI KONTRAK SUKCI ---
-keep class com.azhua.core.contracts.** { *; }

# --- LINDUNGI GSON MODELS ---
-keep class com.azhua.app.data.models.ExtensionItem { *; }

# --- LINDUNGI ROOM ENTITIES ---
-keep class com.azhua.app.data.local.WatchHistory { *; }

# --- LINDUNGI LIBRARIES ---
-keep class org.jsoup.** { *; }
-keep class com.google.gson.** { *; }
-keep class androidx.media3.** { *; }
```

### ext-anichin/proguard-rules.pro
```proguard
# --- LINDUNGI CLASS UTAMA EKSTENSI ---
-keep class com.azhua.ext.anichin.AnichinSource { *; }

# --- LINDUNGI KONTRAK ---
-keep interface com.azhua.core.contracts.Source { *; }
-keep class com.azhua.core.contracts.models.** { *; }
```

## 🏗️ 4. Build APK Release

### Build Core App
```bash
./gradlew :app:assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### Build Ekstensi
```bash
./gradlew :ext-anichin:assembleRelease
```

Output: `ext-anichin/build/outputs/apk/release/ext-anichin-release.apk`

## 📊 Perbandingan Build

| Tipe | Minify | Shrink | Signed | Ukuran |
|------|--------|--------|--------|--------|
| Debug | ❌ | ❌ | Debug | ~25 MB |
| Release | ✅ | ✅ | Keystore | ~12 MB |

## ✅ Checklist Pre-Release

- [x] Keystore dibuat dan disimpan aman
- [x] signingConfigs dikonfigurasi
- [x] isMinifyEnabled = true
- [x] isShrinkResources = true
- [x] proguard-rules.pro lengkap
- [x] Versi app diupdate (versionCode + versionName)
- [x] Ekstensi versi diupdate
- [x] Test install di device sungguhan

## 🚀 Distribusi

### Langkah Release
1. **Build Release APK**
   ```bash
   ./gradlew :app:assembleRelease
   ```

2. **Rename (opsional)**
   ```bash
   mv app-release.apk AzHua-v1.0.0-release.apk
   ```

3. **Verifikasi Signing**
   ```bash
   apksigner verify AzHua-v1.0.0-release.apk
   ```

4. **Upload ke GitHub Releases** (untuk Paviliun)

5. **Share ke Pengguna**
   - WhatsApp/Telegram
   - GitHub Releases
   - Website download

## ⚠️ Catatan Keamanan

### JANGAN COMMIT KEYSTORE KE GIT!
Tambahkan ke `.gitignore`:
```
keystore/
*.jks
*.keystore
```

### Backup Keystore
- Simpan di cloud pribadi (Google Drive, Dropbox)
- Simpan di external drive
- Catat password dengan aman

## 🎉 AZHUA SIAP DILUNCURKAN!

Setelah Jalur 4 ini selesai, AzHua telah menjadi:
- ✅ Aplikasi production-ready
- ✅ Signed dengan keystore pribadi
- ✅ Terobfuscate & optimized
- ✅ Siap distribusi ke publik

**Selamat, Kultivator! AzHua v1.0 telah lahir!** 🎊
