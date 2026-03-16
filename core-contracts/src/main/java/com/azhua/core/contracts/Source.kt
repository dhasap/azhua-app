package com.azhua.core.contracts

import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.Episode
import com.azhua.core.contracts.models.VideoStream

/**
 * Interface yang harus diimplementasikan oleh setiap Ekstensi (Extension).
 * Ini adalah kontrak utama antara Core App dan Extensions.
 */
interface Source {
    /**
     * Unique identifier untuk source ini.
     * Format: lowercase, no spaces, snake_case.
     * Contoh: "animeindo", "kusonime", "samehadaku"
     */
    val id: String

    /**
     * Nama yang ditampilkan untuk source ini.
     * Contoh: "AnimeIndo", "Kusonime", "Samehadaku"
     */
    val name: String

    /**
     * URL base dari website source.
     * Contoh: "https://animeindo.to"
     */
    val baseUrl: String

    /**
     * Versi dari source/extension ini.
     * Format: Semantic Versioning (1.0.0)
     */
    val version: String

    /**
     * Bahasa konten dari source ini.
     * Format: ISO 639-1 (id, en, ja, dll)
     */
    val language: String

    /**
     * Daftar anime yang sedang trending/popular.
     * @return List of Anime
     */
    suspend fun getPopularAnime(page: Int = 1): List<Anime>

    /**
     * Daftar anime yang sedang ongoing/rilis.
     * @return List of Anime
     */
    suspend fun getLatestAnime(page: Int = 1): List<Anime>

    /**
     * Daftar anime yang akan datang/upcoming.
     * @return List of Anime
     */
    suspend fun getUpcomingAnime(page: Int = 1): List<Anime>

    /**
     * Daftar anime yang sudah completed.
     * @return List of Anime
     */
    suspend fun getCompletedAnime(page: Int = 1): List<Anime>

    /**
     * Mencari anime berdasarkan query.
     * @param query Kata kunci pencarian
     * @param page Nomor halaman
     * @param filters Filter tambahan (opsional)
     * @return List of Anime
     */
    suspend fun searchAnime(
        query: String,
        page: Int = 1,
        filters: Map<String, String> = emptyMap()
    ): List<Anime>

    /**
     * Mendapatkan detail lengkap dari sebuah anime.
     * @param animeId ID anime
     * @return Anime object dengan episode list yang terisi
     */
    suspend fun getAnimeDetails(animeId: String): Anime

    /**
     * Mendapatkan daftar episode dari sebuah anime.
     * @param animeId ID anime
     * @return List of Episode
     */
    suspend fun getEpisodes(animeId: String): List<Episode>

    /**
     * Mendapatkan video streams untuk sebuah episode.
     * @param episodeId ID episode
     * @return List of VideoStream (berbagai kualitas)
     */
    suspend fun getVideoStreams(episodeId: String): List<VideoStream>

    /**
     * Mendapatkan daftar genre yang tersedia.
     * @return List of genre names
     */
    suspend fun getGenres(): List<String>

    /**
     * Mendapatkan anime berdasarkan genre.
     * @param genre Nama genre
     * @param page Nomor halaman
     * @return List of Anime
     */
    suspend fun getAnimeByGenre(genre: String, page: Int = 1): List<Anime>
}

/**
 * Factory interface untuk membuat instance Source.
 * Setiap extension harus menyediakan implementasi ini.
 */
interface SourceFactory {
    fun create(): Source
}

/**
 * Data class untuk metadata extension.
 */
data class ExtensionMetadata(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String = "",
    val language: String,
    val baseUrl: String,
    val iconUrl: String = "",
    val requiresLogin: Boolean = false,
    val nsfw: Boolean = false
)
