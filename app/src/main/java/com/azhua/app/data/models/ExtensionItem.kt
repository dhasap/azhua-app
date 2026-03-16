package com.azhua.app.data.models

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk item ekstensi dari repository JSON.
 * Mewakili metadata sebuah ekstensi yang tersedia untuk diunduh.
 */
data class ExtensionItem(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("pkg")
    val pkg: String,
    
    @SerializedName("versionCode")
    val versionCode: Int,
    
    @SerializedName("versionName")
    val versionName: String,
    
    @SerializedName("lang")
    val lang: String,
    
    @SerializedName("icon")
    val iconUrl: String,
    
    @SerializedName("apkUrl")
    val apkUrl: String,
    
    @SerializedName("description")
    val description: String = ""
) {
    /**
     * Format tampilan versi lengkap
     */
    val displayVersion: String
        get() = "v$versionName ($versionCode)"
    
    /**
     * Format tampilan bahasa dengan emoji
     */
    val displayLanguage: String
        get() = when (lang.lowercase()) {
            "id" -> "🇮🇩 Indonesia"
            "en" -> "🇬🇧 English"
            "ja" -> "🇯🇵 日本語"
            "zh" -> "🇨🇳 中文"
            "ko" -> "🇰🇷 한국어"
            else -> "🌐 $lang"
        }
}
