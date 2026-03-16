package com.azhua.ext.anichin.parser

import com.azhua.core.contracts.models.Anime
import com.azhua.core.contracts.models.AnimeStatus
import com.azhua.core.contracts.models.AnimeType
import com.azhua.ext.anichin.network.AnichinClient
import org.jsoup.nodes.Document

/**
 * AnimeDetailParser - Pengurai Detail Donghua
 *
 * Parser untuk halaman detail anime dan informasi lengkapnya
 */
class AnimeDetailParser(private val client: AnichinClient) {

    /**
     * Parse detail anime dari halaman detail
     */
    fun parseAnimeDetail(document: Document?, url: String): Anime? {
        if (document == null) return null

        try {
            // Judul utama
            val title = document.selectFirst("h1.entry-title, h1[itemprop=name], .entry-title")?.text()
                ?: document.selectFirst("title")?.text()?.split(" - ")?.first()
                ?: "Unknown"

            // Judul alternatif (japanese/english title)
            val altTitles = mutableListOf<String>()
            document.select(".alternative, .alter, .alternative-title").forEach {
                altTitles.add(it.text())
            }

            // Deskripsi/sinopsis
            val description = document.selectFirst(".entry-content, .synopsis, .summary, [itemprop=description]")
                ?.text()
                ?.replace(Regex("read more|selengkapnya", RegexOption.IGNORE_CASE), "")
                ?: ""

            // Cover image (biasanya lebih besar di halaman detail)
            val coverUrl = document.selectFirst(".poster img, .thumb img, .wp-post-image")
                ?.let {
                    it.attr("data-src")
                        .ifEmpty { it.attr("data-lazy-src") }
                        .ifEmpty { it.attr("src") }
                }
                ?: ""

            // Banner image (opsional)
            val bannerUrl = document.selectFirst(".banner img, .cover img")
                ?.attr("src")
                ?: coverUrl

            // Info meta (Status, Type, Year, dll)
            val infoMap = mutableMapOf<String, String>()
            document.select(".info-content .info, .infobox .infobox-item, .meta span").forEach { element ->
                val label = element.selectFirst("strong, b, .label")?.text()?.replace(":", "")?.trim()?.lowercase() ?: ""
                val value = element.text().replace(Regex("$label:?", RegexOption.IGNORE_CASE), "").trim()
                if (label.isNotEmpty() && value.isNotEmpty()) {
                    infoMap[label] = value
                }
            }

            // Parse status
            val statusText = infoMap["status"] ?: infoMap["status"]
            val status = when {
                statusText?.contains("ongoing", true) == true -> AnimeStatus.ONGOING
                statusText?.contains("completed", true) == true ||
                    statusText?.contains("end", true) == true -> AnimeStatus.COMPLETED
                statusText?.contains("upcoming", true) == true -> AnimeStatus.UPCOMING
                else -> AnimeStatus.UNKNOWN
            }

            // Parse type
            val typeText = infoMap["type"] ?: "TV"
            val type = when {
                typeText.contains("movie", true) -> AnimeType.MOVIE
                typeText.contains("ova", true) -> AnimeType.OVA
                typeText.contains("ona", true) -> AnimeType.ONA
                typeText.contains("special", true) -> AnimeType.SPECIAL
                else -> AnimeType.TV
            }

            // Parse rating
            val ratingText = infoMap["score"] ?: infoMap["rating"] ?: "0"
            val rating = ratingText.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0

            // Parse year
            val yearText = infoMap["released"] ?: infoMap["year"] ?: ""
            val year = yearText.replace(Regex("[^0-9]"), "").take(4).toIntOrNull() ?: 0

            // Parse genres
            val genres = document.select(".genres a, .genre a, [rel=category tag]").map {
                it.text().trim()
            }.filter { it.isNotEmpty() }

            return Anime(
                id = url.hashCode().toString(),
                title = title,
                alternativeTitles = altTitles,
                description = description,
                coverImage = coverUrl,
                bannerImage = bannerUrl,
                status = status,
                type = type,
                rating = rating,
                releaseYear = year,
                genres = genres,
                sourceUrl = url
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Parse informasi tambahan yang tidak ada di list
     */
    fun parseAdditionalInfo(document: Document?): Map<String, String> {
        val info = mutableMapOf<String, String>()
        if (document == null) return info

        document.select(".info-content .info, .infobox .infobox-item").forEach { element ->
            val label = element.selectFirst("strong, b")?.text()?.replace(":", "")?.trim() ?: ""
            val value = element.text().replace(Regex("$label:?"), "").trim()
            if (label.isNotEmpty() && value.isNotEmpty()) {
                info[label] = value
            }
        }

        return info
    }
}
