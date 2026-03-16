# 🛡️ MANTRA PELINDUNG EKSTENSI ANICHIN
# ====================================
# File ini melindungi class ekstensi agar tetap bisa di-load oleh Core App
# meskipun di-obfuscate.

# --- 1. LINDUNGI CLASS UTAMA EKSTENSI ---
# AnichinSource harus tetap bernama AnichinSource agar bisa di-load via reflection
-keep class com.azhua.ext.anichin.AnichinSource { *; }
-keepclassmembers class com.azhua.ext.anichin.AnichinSource { *; }

# --- 2. LINDUNGI PARSER & NETWORK ---
-keep class com.azhua.ext.anichin.parser.** { *; }
-keep class com.azhua.ext.anichin.network.** { *; }

# --- 3. LINDUNGI KONTRAK (WAJIB!) ---
# compileOnly berarti Core App punya, tapi tetap perlu keep nama interface
-keep interface com.azhua.core.contracts.Source { *; }
-keep interface com.azhua.core.contracts.SourceFactory { *; }

# --- 4. LINDUNGI MODEL ---
-keep class com.azhua.core.contracts.models.** { *; }

# --- 5. LINDUNGI JSOUP ---
-keep class org.jsoup.** { *; }
-keepclassmembers class org.jsoup.** { *; }

# --- 6. LINDUNGI OKHTTP ---
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# --- 7. LINDUNGI KOTLIN ---
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** { *; }

# --- 8. KEEP META-DATA ---
-keepattributes *Annotation*

# --- 9. SUPPRESS MISSING CLASS WARNINGS ---
-dontwarn org.jspecify.annotations.NullMarked
-keepattributes Signature
