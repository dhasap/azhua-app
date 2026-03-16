package com.azhua.core.contracts.models

/**
 * Data class representing a video stream source.
 */
data class VideoStream(
    val id: String,
    val quality: VideoQuality,
    val url: String,
    val headers: Map<String, String> = emptyMap(),
    val isHls: Boolean = false,
    val isDASH: Boolean = false,
    val subtitleTracks: List<SubtitleTrack> = emptyList()
)

enum class VideoQuality {
    UHD_4K,
    QHD_1440P,
    FHD_1080P,
    HD_720P,
    SD_480P,
    SD_360P,
    UNKNOWN
}

data class SubtitleTrack(
    val language: String,
    val url: String,
    val label: String = "",
    val isDefault: Boolean = false
)
