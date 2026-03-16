package com.azhua.core.contracts.models

/**
 * Data class representing an Episode of an Anime.
 */
data class Episode(
    val id: String,
    val number: Int,
    val title: String = "",
    val thumbnail: String = "",
    val description: String = "",
    val duration: Long = 0, // in seconds
    val releaseDate: Long = 0, // timestamp
    val videoStreams: List<VideoStream> = emptyList(),
    val sourceUrl: String = ""
)
