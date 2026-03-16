#!/bin/bash
# ☁️ Build Script untuk Ekstensi AzHua
# 
# Usage: ./build-extension.sh [module-name] [version]
# Contoh: ./build-extension.sh ext-anichin 2.0.0

set -e

MODULE_NAME=${1:-"ext-anichin"}
VERSION=${2:-"2.0.0"}

echo "🏗️  Building $MODULE_NAME v$VERSION..."

# Build release APK
./gradlew :$MODULE_NAME:assembleRelease

# Create output directory
mkdir -p releases

# Copy and rename APK
SOURCE_PATH="$MODULE_NAME/build/outputs/apk/release/${MODULE_NAME}-release-unsigned.apk"
DEST_PATH="releases/${MODULE_NAME}-v${VERSION}.apk"

if [ -f "$SOURCE_PATH" ]; then
    cp "$SOURCE_PATH" "$DEST_PATH"
    echo "✅ APK berhasil dibuat: $DEST_PATH"
    echo ""
    echo "📋 Langkah selanjutnya:"
    echo "1. Upload $DEST_PATH ke GitHub Releases"
    echo "2. Update index.json di repo azhua-extensions"
    echo "3. Push perubahan index.json"
else
    echo "❌ APK tidak ditemukan di $SOURCE_PATH"
    exit 1
fi
