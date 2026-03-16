package com.azhua.ext.anichin

import android.util.Log
import com.azhua.core.contracts.Source
import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.AnimeStatus
import com.azhua.core.contracts.models.AnimeType
import com.azhua.core.contracts.models.Episode
import com.azhua.core.contracts.models.VideoQuality
import com.azhua.core.contracts.models.VideoStream
import com.azhua.ext.anichin.network.AnichinClient
import com.azhua.ext.anichin.parser.AnimeDetailParser
import com.azhua.ext.anichin.parser.AnimeListParser
import com.azhua.ext.anichin.parser.EpisodeParser
import com.azhua.ext.anichin.parser.VideoStreamParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AnichinSource - Artefak Penjelajah Anichin
 *
 * Implementasi Source interface untuk website Anichin (anichin.co.id)
 * Menggunakan teknik web scraping nyata dengan Jsoup untuk mengekstrak data Donghua.
 *
 * Teknik yang diterapkan:
 * - HTTP request dengan User-Agent spoofing
 * - HTML DOM parsing dengan Jsoup
 * - Regex pattern matching untuk link video
 * - Base64 decoding untuk iframe tersembunyi
 * - Multi-server extraction (Server 1, 2, 3, dll)
 */
class AnichinSource : Source {

    companion object {
        private const val TAG = "AnichinSource"
    }

    override val id: String = "anichin"
    override val name: String = "Anichin"
    override val baseUrl: String = "https://anichin.co.id"
    override val version: String = "2.0.0"
    override val language: String = "id"

    // Inisialisasi client dan parser
    private val client = AnichinClient()
    private val listParser = AnimeListParser(client)
    private val detailParser = AnimeDetailParser(client)
    private val episodeParser = EpisodeParser(client)
    private val videoParser = VideoStreamParser(client)

    /**
     * Mengambil daftar anime populer dari halaman utama
     * URL: https://anichin.co.id/
     */
    override suspend fun getPopularAnime(page: Int): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val url = if (page > 1) "$baseUrl/page/$page/" else baseUrl
            Log.d(TAG, "Fetching popular anime from: $url")

            val document = client.getDocument(url)
            val animeList = listParser.parseAnimeList(document)

            Log.d(TAG, "Found ${animeList.size} popular anime")
            return@withContext animeList
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching popular anime", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mengambil daftar anime terbaru/rilis
     * URL: https://anichin.co.id/anime/?status=ongoing&order=update
     */
    override suspend fun getLatestAnime(page: Int): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/anime/?status=ongoing&order=update"
            if (page > 1) "$url&page=$page" else url

            Log.d(TAG, "Fetching latest anime from: $url")

            val document = client.getDocument(url)
            val animeList = listParser.parseAnimeList(document)

            Log.d(TAG, "Found ${animeList.size} latest anime")
            return@withContext animeList
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching latest anime", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mengambil daftar anime upcoming
     */
    override suspend fun getUpcomingAnime(page: Int): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/anime/?status=upcoming"
            Log.d(TAG, "Fetching upcoming anime from: $url")

            val document = client.getDocument(url)
            val animeList = listParser.parseAnimeList(document)

            return@withContext animeList
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching upcoming anime", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mengambil daftar anime completed
     */
    override suspend fun getCompletedAnime(page: Int): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/anime/?status=completed"
            Log.d(TAG, "Fetching completed anime from: $url")

            val document = client.getDocument(url)
            val animeList = listParser.parseAnimeList(document)

            return@withContext animeList
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching completed anime", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mencari anime berdasarkan query
     * URL: https://anichin.co.id/?s={query}
     */
    override suspend fun searchAnime(
        query: String,
        page: Int,
        filters: Map<String, String>
    ): List<Anime> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        try {
            val url = "$baseUrl/"
            Log.d(TAG, "Searching for: $query")

            // Anichin biasanya menggunakan form POST untuk search
            val formData = mutableMapOf("s" to query)

            // Tambahkan filter jika ada
            filters["genre"]?.let { formData["genre"] = it }
            filters["status"]?.let { formData["status"] = it }
            filters["type"]?.let { formData["type"] = it }

            val document = client.postDocument(url, formData)
            val animeList = listParser.parseSearchResults(document)

            Log.d(TAG, "Found ${animeList.size} search results")
            return@withContext animeList
        } catch (e: Exception) {
            Log.e(TAG, "Error searching anime", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mendapatkan detail lengkap anime termasuk daftar episode
     */
    override suspend fun getAnimeDetails(animeId: String): Anime = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching anime details from: $animeId")

            val document = client.getDocument(animeId)

            // Parse detail
            val anime = detailParser.parseAnimeDetail(document, animeId)
                ?: return@withContext Anime(
                    id = animeId,
                    title = "Error Loading",
                    sourceUrl = animeId
                )

            // Parse episodes
            val episodes = episodeParser.parseEpisodeList(document, animeId)

            return@withContext anime.copy(episodes = episodes)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching anime details", e)
            return@withContext Anime(
                id = animeId,
                title = "Error: ${e.message}",
                sourceUrl = animeId
            )
        }
    }

    /**
     * Mendapatkan daftar episode dari anime
     */
    override suspend fun getEpisodes(animeId: String): List<Episode> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching episodes from: $animeId")

            val document = client.getDocument(animeId)
            val episodes = episodeParser.parseEpisodeList(document, animeId)

            Log.d(TAG, "Found ${episodes.size} episodes")
            return@withContext episodes
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching episodes", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mendapatkan link streaming video dari episode
     * INI ADALAH TEKNIK PALING DALAM - Ekstraksi .m3u8!
     */
    override suspend fun getVideoStreams(episodeId: String): List<VideoStream> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching video streams from: $episodeId")

            // Load halaman episode
            val document = client.getDocument(episodeId)

            if (document == null) {
                Log.e(TAG, "Failed to load episode page")
                return@withContext emptyList()
            }

            // Coba ekstrak video streams
            var streams = videoParser.parseVideoStreams(document, episodeId)

            // Jika tidak ditemukan, coba cek server alternatif
            if (streams.isEmpty()) {
                val servers = videoParser.parseServerLinks(document)

                for ((serverName, serverUrl) in servers) {
                    Log.d(TAG, "Trying server: $serverName")
                    val serverDoc = client.getDocument(serverUrl)
                    val serverStreams = videoParser.parseVideoStreams(serverDoc, serverUrl)

                    if (serverStreams.isNotEmpty()) {
                        streams = serverStreams
                        break
                    }
                }
            }

            Log.d(TAG, "Found ${streams.size} video streams")
            return@withContext streams
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching video streams", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mendapatkan daftar genre yang tersedia
     */
    override suspend fun getGenres(): List<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching genres")

            val document = client.getDocument(baseUrl)
            val genres = mutableListOf<String>()

            document?.select("ul.genre-menu li a, .genre-item a, a[href*=genre]")?.forEach { element ->
                val genreName = element.text().trim()
                if (genreName.isNotEmpty() && genreName.length < 30) {
                    genres.add(genreName)
                }
            }

            return@withContext genres.distinct()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching genres", e)
            return@withContext emptyList()
        }
    }

    /**
     * Mendapatkan anime berdasarkan genre
     */
    override suspend fun getAnimeByGenre(genre: String, page: Int): List<Anime> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/genre/$genre/"
            Log.d(TAG, "Fetching anime by genre from: $url")

            val document = client.getDocument(url)
            val animeList = listParser.parseAnimeList(document)

            return@withContext animeList
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching anime by genre", e)
            return@withContext emptyList()
        }
    }
}
