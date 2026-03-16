package com.azhua.core.contracts.models

/**
 * Data class representing an Anime entity.
 * This is the core model used across all extensions and the main app.
 */
data class Anime(
    val id: String,
    val title: String,
    val alternativeTitles: List<String> = emptyList(),
    val description: String = "",
    val coverImage: String = "",
    val bannerImage: String = "",
    val status: AnimeStatus = AnimeStatus.UNKNOWN,
    val type: AnimeType = AnimeType.TV,
    val rating: Double = 0.0,
    val releaseYear: Int = 0,
    val genres: List<String> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    val sourceUrl: String = "",
    val additionalInfo: Map<String, String> = emptyMap()
)

enum class AnimeStatus {
    ONGOING,
    COMPLETED,
    UPCOMING,
    HIATUS,
    CANCELLED,
    UNKNOWN
}

enum class AnimeType {
    TV,
    MOVIE,
    OVA,
    ONA,
    SPECIAL,
    MUSIC,
    UNKNOWN
}
