#!/bin/bash
# 🛡️ Build Script untuk AZHUA RELEASE
# ==================================
# Script ini mem-build APK release untuk Core App dan Ekstensi
# 
# Usage: ./scripts/build-release.sh

set -e

echo "🏗️  AZHUA RELEASE BUILD SCRIPT"
echo "=============================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if keystore exists
if [ ! -f "keystore/azhua_keystore.jks" ]; then
    echo -e "${RED}❌ ERROR: Keystore tidak ditemukan!${NC}"
    echo "   Path yang dicari: keystore/azhua_keystore.jks"
    echo ""
    echo "   Buat keystore terlebih dahulu dengan command:"
    echo "   keytool -genkey -v -keystore keystore/azhua_keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias azhua_key"
    exit 1
fi

echo -e "${GREEN}✅ Keystore ditemukan${NC}"
echo ""

# Create output directory
mkdir -p releases

# ============================================
# BUILD 1: EXT-ANICHIN (Extension)
# ============================================
echo -e "${YELLOW}📦 Building ext-anichin...${NC}"
./gradlew :ext-anichin:assembleRelease --quiet

# Check if build succeeded
if [ -f "ext-anichin/build/outputs/apk/release/ext-anichin-release.apk" ]; then
    cp ext-anichin/build/outputs/apk/release/ext-anichin-release.apk \
       releases/ext-anichin-v2.0.0.apk
    echo -e "${GREEN}✅ Extension build success!${NC}"
    echo "   Output: releases/ext-anichin-v2.0.0.apk"
else
    echo -e "${RED}❌ Extension build failed!${NC}"
    exit 1
fi

echo ""

# ============================================
# BUILD 2: APP (Core App)
# ============================================
echo -e "${YELLOW}📱 Building Core App...${NC}"
./gradlew :app:assembleRelease --quiet

# Check if build succeeded
if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    cp app/build/outputs/apk/release/app-release.apk \
       releases/AzHua-v1.0.0-release.apk
    echo -e "${GREEN}✅ Core App build success!${NC}"
    echo "   Output: releases/AzHua-v1.0.0-release.apk"
else
    echo -e "${RED}❌ Core App build failed!${NC}"
    exit 1
fi

echo ""
echo "=============================="
echo -e "${GREEN}🎉 BUILD SELESAI!${NC}"
echo "=============================="
echo ""
echo "📁 Output files:"
ls -lh releases/
echo ""
echo "🚀 Langkah selanjutnya:"
echo "   1. Test APK di device sungguhan"
echo "   2. Upload ext-anichin ke GitHub Releases"
echo "   3. Update index.json di repo azhua-extensions"
echo "   4. Distribusi AzHua ke pengguna!"
echo ""
