package com.azhua.core.contracts.samples

import com.azhua.core.contracts.ExtensionMetadata
import com.azhua.core.contracts.Source
import com.azhua.core.contracts.SourceFactory
import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.AnimeStatus
import com.azhua.core.contracts.models.AnimeType
import com.azhua.core.contracts.models.Episode
import com.azhua.core.contracts.models.VideoQuality
import com.azhua.core.contracts.models.VideoStream

/**
 * Contoh implementasi Source interface.
 * Extension developer harus mengimplementasikan interface ini.
 */
class SampleSourceImpl : Source {
    override val id: String = "sample_anime_source"
    override val name: String = "Sample Anime Source"
    override val baseUrl: String = "https://example-anime.com"
    override val version: String = "1.0.0"
    override val language: String = "id"

    override suspend fun getPopularAnime(page: Int): List<Anime> {
        // Implementasi scraping/popular anime
        return emptyList()
    }

    override suspend fun getLatestAnime(page: Int): List<Anime> {
        // Implementasi scraping/latest releases
        return emptyList()
    }

    override suspend fun getUpcomingAnime(page: Int): List<Anime> {
        return emptyList()
    }

    override suspend fun getCompletedAnime(page: Int): List<Anime> {
        return emptyList()
    }

    override suspend fun searchAnime(
        query: String,
        page: Int,
        filters: Map<String, String>
    ): List<Anime> {
        // Implementasi pencarian
        return emptyList()
    }

    override suspend fun getAnimeDetails(animeId: String): Anime {
        // Implementasi detail anime + daftar episode
        return Anime(
            id = animeId,
            title = "Sample Anime",
            status = AnimeStatus.COMPLETED,
            type = AnimeType.TV
        )
    }

    override suspend fun getEpisodes(animeId: String): List<Episode> {
        return emptyList()
    }

    override suspend fun getVideoStreams(episodeId: String): List<VideoStream> {
        // Implementasi ekstrak link video
        return listOf(
            VideoStream(
                id = "stream_1",
                quality = VideoQuality.FHD_1080P,
                url = "https://example.com/video.m3u8",
                isHls = true
            )
        )
    }

    override suspend fun getGenres(): List<String> {
        return listOf("Action", "Adventure", "Comedy", "Drama", "Fantasy")
    }

    override suspend fun getAnimeByGenre(genre: String, page: Int): List<Anime> {
        return emptyList()
    }
}

/**
 * Factory untuk membuat instance SampleSourceImpl.
 * Setiap extension WAJIB menyediakan factory ini.
 */
class SampleSourceFactory : SourceFactory {
    override fun create(): Source = SampleSourceImpl()
}

/**
 * Metadata untuk extension ini.
 */
val SampleExtensionMetadata = ExtensionMetadata(
    id = "sample_anime_source",
    name = "Sample Anime Source",
    version = "1.0.0",
    author = "AzHua Team",
    description = "Contoh implementasi source untuk AzHua",
    language = "id",
    baseUrl = "https://example-anime.com"
)
