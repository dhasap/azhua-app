package com.azhua.ext.anichin.parser

import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.AnimeStatus
import com.azhua.core.contracts.models.AnimeType
import com.azhua.ext.anichin.network.AnichinClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * AnimeListParser - Pengurai Daftar Donghua
 *
 * Parser untuk halaman list anime: popular, latest, search, genre
 */
class AnimeListParser(private val client: AnichinClient) {

    /**
     * Parse anime list dari halaman popular/latest
     * URL: / or /anime/ or /genre/{genre}/
     */
    fun parseAnimeList(document: Document?): List<Anime> {
        if (document == null) return emptyList()

        val animeList = mutableListOf<Anime>()

        // Selector untuk card anime (bsx = box series)
        val elements = document.select("div.bsx, div.utao, article.item")

        elements.forEach { element ->
            try {
                val anime = parseAnimeItem(element)
                if (anime != null) {
                    animeList.add(anime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return animeList
    }

    /**
     * Parse satu item anime dari element
     */
    private fun parseAnimeItem(element: Element): Anime? {
        // Link dan judul
        val linkElement = element.selectFirst("a[href]") ?: return null
        val url = linkElement.attr("abs:href")
        val title = linkElement.attr("title")
            .ifEmpty { element.selectFirst("h2, h3, .tt")?.text() }
            ?: "Unknown"

        // Cover image
        val imgElement = element.selectFirst("img")
        val coverUrl = imgElement?.let {
            it.attr("data-src")
                .ifEmpty { it.attr("data-lazy-src") }
                .ifEmpty { it.attr("src") }
        } ?: ""

        // Status (dari badge)
        val statusText = element.selectFirst(".sb, .status, .type")?.text()?.uppercase() ?: ""
        val status = when {
            statusText.contains("ONGOING") -> AnimeStatus.ONGOING
            statusText.contains("COMPLETED") || statusText.contains("END") -> AnimeStatus.COMPLETED
            else -> AnimeStatus.UNKNOWN
        }

        // Type (TV/Movie/OVA)
        val typeText = element.selectFirst(".type, .sb")?.text()?.uppercase() ?: "TV"
        val type = when {
            typeText.contains("MOVIE") -> AnimeType.MOVIE
            typeText.contains("OVA") -> AnimeType.OVA
            typeText.contains("ONA") -> AnimeType.ONA
            typeText.contains("SPECIAL") -> AnimeType.SPECIAL
            else -> AnimeType.TV
        }

        // Rating (jika ada)
        val ratingText = element.selectFirst(".rating, .score")?.text() ?: ""
        val rating = ratingText.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0

        // Tahun rilis (dari teks)
        val yearText = element.selectFirst(".year, .date")?.text() ?: ""
        val year = yearText.replace(Regex("[^0-9]"), "").take(4).toIntOrNull() ?: 0

        return Anime(
            id = url.hashCode().toString(), // Generate ID dari hash URL
            title = title,
            coverImage = coverUrl,
            status = status,
            type = type,
            rating = rating,
            releaseYear = year,
            sourceUrl = url
        )
    }

    /**
     * Parse hasil pencarian
     */
    fun parseSearchResults(document: Document?): List<Anime> {
        if (document == null) return emptyList()

        val animeList = mutableListOf<Anime>()

        // Selector untuk hasil pencarian
        val elements = document.select("div.listupd article, div.bsx, .search-result .item")

        elements.forEach { element ->
            try {
                val anime = parseAnimeItem(element)
                if (anime != null) {
                    animeList.add(anime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return animeList
    }

    /**
     * Ambil URL halaman selanjutnya (pagination)
     */
    fun getNextPageUrl(document: Document?): String? {
        if (document == null) return null

        // Cari link "Next" atau halaman selanjutnya
        val nextLink = document.selectFirst("a.next, .pagination a[rel=next], a:contains(Next)")
        return nextLink?.attr("abs:href")
    }
}
