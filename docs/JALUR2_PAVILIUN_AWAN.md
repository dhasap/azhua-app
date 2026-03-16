# ☁️ Jalur 2: Membangun Paviliun Awan (GitHub Repository)

## Ringkasan

Jalur ini membuat repository GitHub publik sebagai pusat distribusi ekstensi (App Store mini) untuk AzHua.

## 🏛️ Repository GitHub

**URL**: https://github.com/dhasap/azhua-extensions

### Struktur Repository

```
azhua-extensions/
├── index.json          # Katalog ekstensi (daftar tersedia)
├── icons/              # Ikon ekstensi (PNG)
│   └── anichin.png
└── README.md           # Dokumentasi
```

## 📋 Format index.json

```json
[
  {
    "name": "Anichin",
    "pkg": "com.azhua.ext.anichin",
    "versionCode": 2,
    "versionName": "2.0.0",
    "lang": "id",
    "icon": "https://raw.githubusercontent.com/dhasap/azhua-extensions/main/icons/anichin.png",
    "apkUrl": "https://github.com/dhasap/azhua-extensions/releases/download/v2.0.0/ext-anichin-v2.0.0.apk",
    "description": "Ekstensi scraping untuk anichin.co.id"
  }
]
```

## 🚀 Workflow Release Ekstensi

### 1. Build APK Release

```bash
# Di project AzHua
./gradlew :ext-anichin:assembleRelease
```

### 2. Rename & Sign (Opsional)

```bash
# Rename
mv ext-anichin/build/outputs/apk/release/ext-anichin-release-unsigned.apk \
   ext-anichin-v2.0.0.apk

# Sign dengan keystore (jika ada)
# jarsigner -keystore azhua.jks ext-anichin-v2.0.0.apk alias_name
```

### 3. Upload ke GitHub Releases

1. Buka https://github.com/dhasap/azhua-extensions/releases/new
2. Buat tag: `v2.0.0`
3. Upload APK: `ext-anichin-v2.0.0.apk`
4. Publish release

### 4. Update index.json

```bash
# Edit index.json, tambah/update entry
git add index.json
git commit -m "Update: Anichin v2.0.0"
git push origin main
```

## 🔗 URL Penting

| Tujuan | URL |
|--------|-----|
| Raw JSON | `https://raw.githubusercontent.com/dhasap/azhua-extensions/main/index.json` |
| Repository | `https://github.com/dhasap/azhua-extensions` |
| Releases | `https://github.com/dhasap/azhua-extensions/releases` |

## 📱 Integrasi Core App

ExtensionManager sudah dikonfigurasi:

```kotlin
private val repoUrl = "https://raw.githubusercontent.com/dhasap/azhua-extensions/main/index.json"
```

## ✅ Checklist Jalur 2

- [x] Repository GitHub: `azhua-extensions`
- [x] File `index.json` dengan data Anichin
- [x] Folder `icons/`
- [x] README.md dokumentasi
- [x] ExtensionManager URL terupdate
- [x] ExtensionItem dengan field description

## 🧪 Testing

1. Buka tab Paviliun di AzHua
2. Core App fetch dari GitHub raw URL
3. List ekstensi muncul (Anichin v2.0.0)
4. Klik Download → APK diunduh dari GitHub Releases
5. Install berhasil!

## 🎯 Next: Jalur 4 (Release & Production)

Setelah Paviliun Awan berdiri:
- Build signed APK Core App
- Setup ProGuard
- Release ke pengguna!

**Paviliun Awan telah terbentang di langit!** ☁️✨
