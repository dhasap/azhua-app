# 🛡️ MANTRA PELINDUNG AZHUA - ProGuard Rules
# ==========================================
# File ini melindungi class-class penting dari obfuscation (pengacakan nama)
# agar arsitektur modular tetap berfungsi setelah release build.

# --- 1. LINDUNGI KONTRAK SUKI (CORE CONTRACTS) ---
# Ini PENTING! Jika Source interface diobfuscate, Ekstensi tidak bisa dimuat.
-keep class com.azhua.core.contracts.** { *; }
-keep interface com.azhua.core.contracts.** { *; }
-keepclassmembers class com.azhua.core.contracts.** { *; }

# --- 2. LINDUNGI GSON MODELS ---
# Model untuk parsing JSON dari GitHub (index.json)
-keep class com.azhua.app.data.models.ExtensionItem { *; }
-keepclassmembers class com.azhua.app.data.models.ExtensionItem { *; }

# --- 3. LINDUNGI ROOM DATABASE ENTITIES ---
-keep class com.azhua.app.data.local.WatchHistory { *; }
-keepclassmembers class com.azhua.app.data.local.WatchHistory {
    <fields>;
    <methods>;
}

# --- 4. LINDUNGI KOTLIN COROUTINES ---
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# --- 5. LINDUNGI COMPOSE (OPSIONAL, TAPI AMAN) ---
-keep class androidx.compose.** { *; }

# --- 6. LINDUNGI JSOUP (JIKA ADA DI CORE) ---
-keep class org.jsoup.** { *; }
-keepclassmembers class org.jsoup.** { *; }

# --- 7. LINDUNGI MEDIA3/EXOPLAYER ---
-keep class androidx.media3.** { *; }

# --- 8. LINDUNGI OKHTTP & GSON ---
-keep class com.google.gson.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# --- 9. JANGAN HAPUS LOG DARI RELEASE (OPSIONAL) ---
# Jika ingin hapus Log.d di release, comment baris ini:
# -assumenosideeffects class android.util.Log {
#     public static int d(...);
#     public static int v(...);
# }

# --- 10. KEEP ANNOTATIONS ---
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes Deprecated
-keepattributes SourceFile
-keepattributes LineNumberTable
