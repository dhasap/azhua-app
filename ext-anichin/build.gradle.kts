plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.azhua.ext.anichin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.azhua.ext.anichin"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "2.0.0"
    }

    buildTypes {
        release {
            // 🛡️ AKTIFKAN OBFUSCATION UNTUK EKSTENSI JUGA
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // PENTING: Gunakan 'compileOnly' untuk core-contracts.
    // Ini memastikan kontrak tidak ikut di-compile ke dalam APK Ekstensi
    // karena Core App sudah memilikinya di memori (mencegah ClassCastException).
    compileOnly(project(":core-contracts"))

    // Library untuk Web Scraping (Parsing HTML)
    implementation("org.jsoup:jsoup:1.17.2")
    
    // Coroutines untuk async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // OkHttp untuk HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
