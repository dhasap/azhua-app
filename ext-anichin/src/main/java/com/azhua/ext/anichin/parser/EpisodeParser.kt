package com.azhua.ext.anichin.parser

import com.azhua.core.contracts.models.Episode
import com.azhua.ext.anichin.network.AnichinClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * EpisodeParser - Pengurai Daftar Episode
 *
 * Parser untuk mengambil daftar episode dari halaman anime
 */
class EpisodeParser(private val client: AnichinClient) {

    /**
     * Parse daftar episode dari halaman detail anime
     */
    fun parseEpisodeList(document: Document?, baseUrl: String): List<Episode> {
        if (document == null) return emptyList()

        val episodes = mutableListOf<Episode>()

        // Coba berbagai selector yang umum digunakan
        val episodeElements = document.select(".eplister li, .episodelist li, .episode-item, .eps li")

        episodeElements.forEachIndexed { index, element ->
            try {
                val episode = parseEpisodeItem(element, index + 1)
                if (episode != null) {
                    episodes.add(episode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Urutkan berdasarkan nomor episode
        return episodes.sortedBy { it.number }
    }

    /**
     * Parse satu episode dari element
     */
    private fun parseEpisodeItem(element: Element, defaultNumber: Int): Episode? {
        // Link episode
        val linkElement = element.selectFirst("a[href]") ?: return null
        val url = linkElement.attr("abs:href")

        // Nomor episode
        val numberText = element.selectFirst(".epl-num, .epnum, .num, .episode-number")
            ?.text()
            ?: linkElement.text()
        val number = numberText.replace(Regex("[^0-9]"), "").toIntOrNull() ?: defaultNumber

        // Judul episode
        val title = element.selectFirst(".epl-title, .eptitle, .title")
            ?.text()
            ?: "Episode $number"

        // Thumbnail (jika ada)
        val thumbnail = element.selectFirst("img")?.let {
            it.attr("data-src")
                .ifEmpty { it.attr("src") }
        } ?: ""

        // Durasi (jika ada)
        val durationText = element.selectFirst(".duration, .time")?.text() ?: ""
        val duration = parseDuration(durationText)

        // Tanggal rilis (jika ada)
        val releaseDate = element.selectFirst(".epl-date, .date")?.text() ?: ""

        return Episode(
            id = url.hashCode().toString(),
            number = number,
            title = title,
            thumbnail = thumbnail,
            duration = duration,
            sourceUrl = url
        )
    }

    /**
     * Parse durasi dari teks (misal: "24:30" atau "24 menit")
     */
    private fun parseDuration(durationText: String): Long {
        return try {
            when {
                // Format MM:SS
                durationText.contains(":") -> {
                    val parts = durationText.split(":")
                    val minutes = parts[0].toLongOrNull() ?: 0
                    val seconds = parts[1].toLongOrNull() ?: 0
                    (minutes * 60) + seconds
                }
                // Format "24 min"
                durationText.contains("min", true) -> {
                    durationText.replace(Regex("[^0-9]"), "").toLongOrNull()?.times(60) ?: 0
                }
                // Format angka murni (detik)
                else -> durationText.toLongOrNull() ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Cek apakah halaman memiliki list episode
     */
    fun hasEpisodeList(document: Document?): Boolean {
        if (document == null) return false
        return document.select(".eplister, .episodelist, .episode-item").isNotEmpty()
    }

    /**
     * Ambil total jumlah episode
     */
    fun getTotalEpisodes(document: Document?): Int {
        return document?.select(".eplister li, .episodelist li")?.size ?: 0
    }
}
