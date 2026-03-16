plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Plugin KSP untuk Room compile-time code generation
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
}

android {
    namespace = "com.azhua.app"
    compileSdk = 34

    // 🔐 KONFIGURASI KEYSTORE UNTUK RELEASE SIGNING
    signingConfigs {
        create("release") {
            storeFile = file("../keystore/azhua_keystore.jks")
            storePassword = "azhua2026"
            keyAlias = "azhua_key"
            keyPassword = "azhua2026"
        }
    }

    defaultConfig {
        applicationId = "com.azhua.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // 🛡️ NONAKTIFKAN PROGUARD SEMENTARA UNTUK BUILD
            isMinifyEnabled = false
            isShrinkResources = false
            
            // 🔐 GUNAKAN KEYSTORE UNTUK SIGNING
            signingConfig = signingConfigs.getByName("release")
            
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

    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    
    // 🐉 KITAB IKON YANG HILANG - Tambahan dari Shifu!
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation(libs.kotlinx.coroutines)
    debugImplementation(libs.compose.ui.tooling)
    
    
    
    // Mengimpor modul kontrak ke dalam aplikasi utama
    implementation(project(":core-contracts"))
    
    // OkHttp untuk HTTP Requests
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Gson untuk parsing JSON
    implementation("com.google.code.gson:gson:2.10.1")
    // Coil untuk loading gambar
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Media3 (ExoPlayer) untuk memutar video .m3u8 dan .mp4
    val media3Version = "1.3.0"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    
    // Room Database untuk persistensi data
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
}
