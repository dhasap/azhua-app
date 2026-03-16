package com.azhua.ext.anichin.parser

import android.util.Base64
import com.azhua.core.contracts.models.VideoQuality
import com.azhua.core.contracts.models.VideoStream
import com.azhua.ext.anichin.network.AnichinClient
import org.jsoup.nodes.Document
import java.util.regex.Pattern

/**
 * VideoStreamParser - Pengurai Aliran Qi (Video Streams)
 *
 * Parser untuk mengekstrak link video (.m3u8, .mp4) dari halaman episode.
 * Ini adalah teknik paling dalam: menembus iframe, decoding base64, regex patterns.
 */
class VideoStreamParser(private val client: AnichinClient) {

    companion object {
        // Pattern untuk mendeteksi URL m3u8
        val M3U8_PATTERN = Pattern.compile(
            "(https?://[^\\s\"'<>]+\\.m3u8[^\\s\"'<>]*)",
            Pattern.CASE_INSENSITIVE
        )

        // Pattern untuk mendeteksi URL mp4
        val MP4_PATTERN = Pattern.compile(
            "(https?://[^\\s\"'<>]+\\.mp4[^\\s\"'<>]*)",
            Pattern.CASE_INSENSITIVE
        )

        // Pattern untuk data base64
        val BASE64_PATTERN = Pattern.compile(
            "data:text/html;base64,([A-Za-z0-9+/=]+)"
        )

        // Pattern untuk eval JavaScript (kadang digunakan untuk obfuscation)
        val EVAL_PATTERN = Pattern.compile(
            "eval\\(function\\(p,a,c,k,e,d\\).*\\)"
        )
    }

    /**
     * Parse video streams dari halaman episode
     * Ini adalah fungsi utama yang akan mencoba berbagai metode ekstraksi
     */
    fun parseVideoStreams(document: Document?, episodeUrl: String): List<VideoStream> {
        if (document == null) return emptyList()

        val streams = mutableListOf<VideoStream>()

        try {
            // Method 1: Cari link m3u8 langsung di HTML
            val html = document.html()
            val m3u8Matches = extractM3U8Urls(html)
            streams.addAll(m3u8Matches.mapIndexed { index, url ->
                VideoStream(
                    id = "stream_$index",
                    quality = detectQuality(url),
                    url = url,
                    isHls = true
                )
            })

            // Method 2: Cari di dalam iframe/embed
            if (streams.isEmpty()) {
                val iframeStreams = extractFromIframe(document)
                streams.addAll(iframeStreams)
            }

            // Method 3: Cari data base64
            if (streams.isEmpty()) {
                val base64Streams = extractFromBase64(html)
                streams.addAll(base64Streams)
            }

            // Method 4: Cari di script tags
            if (streams.isEmpty()) {
                val scriptStreams = extractFromScripts(document)
                streams.addAll(scriptStreams)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return streams.distinctBy { it.url }
    }

    /**
     * Ekstrak URL m3u8 dari teks menggunakan regex
     */
    private fun extractM3U8Urls(text: String): List<String> {
        val urls = mutableListOf<String>()
        val matcher = M3U8_PATTERN.matcher(text)
        while (matcher.find()) {
            urls.add(matcher.group(1))
        }
        return urls.filter { it.isNotEmpty() }
    }

    /**
     * Ekstrak video dari iframe/embed
     * Biasanya player terpisah seperti StreamWish, Dood, dll
     */
    private fun extractFromIframe(document: Document): List<VideoStream> {
        val streams = mutableListOf<VideoStream>()

        // Cari iframe
        val iframes = document.select("iframe[src], embed[src]")

        for (iframe in iframes) {
            val src = iframe.attr("abs:src")
            if (src.isNotEmpty()) {
                // Load halaman iframe
                val iframeDoc = client.getDocument(src)
                if (iframeDoc != null) {
                    val iframeHtml = iframeDoc.html()
                    val urls = extractM3U8Urls(iframeHtml)

                    streams.addAll(urls.mapIndexed { index, url ->
                        VideoStream(
                            id = "iframe_stream_$index",
                            quality = detectQuality(url),
                            url = url,
                            isHls = true
                        )
                    })
                }
            }
        }

        return streams
    }

    /**
     * Ekstrak dari data base64 (biasanya iframe tersembunyi)
     */
    private fun extractFromBase64(html: String): List<VideoStream> {
        val streams = mutableListOf<VideoStream>()

        val matcher = BASE64_PATTERN.matcher(html)
        while (matcher.find()) {
            try {
                val base64Data = matcher.group(1)
                val decoded = String(Base64.decode(base64Data, Base64.DEFAULT))

                // Cari URL m3u8 di dalam hasil decode
                val urls = extractM3U8Urls(decoded)
                streams.addAll(urls.mapIndexed { index, url ->
                    VideoStream(
                        id = "base64_stream_$index",
                        quality = detectQuality(url),
                        url = url,
                        isHls = true
                    )
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return streams
    }

    /**
     * Ekstrak dari script tags (JavaScript variables)
     */
    private fun extractFromScripts(document: Document): List<VideoStream> {
        val streams = mutableListOf<VideoStream>()

        document.select("script").forEach { script ->
            val scriptText = script.data()

            // Cari variabel yang mengandung URL video
            val patterns = listOf(
                "sources\\s*:\\s*\\[([^\\]]+)\\]",
                "file\":\"([^\"]+)\"",
                "src\":\"([^\"]+\\.m3u8[^\"]*)\"",
                "videoUrl\\s*=\\s*['\"]([^'\"]+)['\"]"
            )

            patterns.forEach { patternStr ->
                try {
                    val pattern = Pattern.compile(patternStr)
                    val matcher = pattern.matcher(scriptText)
                    while (matcher.find()) {
                        val url = matcher.group(1)
                        if (url.contains(".m3u8") || url.contains(".mp4")) {
                            streams.add(VideoStream(
                                id = "script_stream_${streams.size}",
                                quality = detectQuality(url),
                                url = url,
                                isHls = url.contains(".m3u8")
                            ))
                        }
                    }
                } catch (e: Exception) {
                    // Continue to next pattern
                }
            }
        }

        return streams
    }

    /**
     * Deteksi kualitas video dari URL
     * Contoh: ".m3u8?res=1080" atau "/1080/" atau "_1080p"
     */
    private fun detectQuality(url: String): VideoQuality {
        val lowerUrl = url.lowercase()

        return when {
            lowerUrl.contains("4k") || lowerUrl.contains("2160") -> VideoQuality.UHD_4K
            lowerUrl.contains("1440") || lowerUrl.contains("qhd") -> VideoQuality.QHD_1440P
            lowerUrl.contains("1080") || lowerUrl.contains("fhd") -> VideoQuality.FHD_1080P
            lowerUrl.contains("720") || lowerUrl.contains("hd") -> VideoQuality.HD_720P
            lowerUrl.contains("480") -> VideoQuality.SD_480P
            lowerUrl.contains("360") -> VideoQuality.SD_360P
            else -> VideoQuality.UNKNOWN
        }
    }

    /**
     * Mendapatkan URL langsung dari link mirror/server
     * Kadang anichin punya multiple server (Server 1, Server 2, dll)
     */
    fun parseServerLinks(document: Document?): Map<String, String> {
        val servers = mutableMapOf<String, String>()
        if (document == null) return servers

        document.select(".server-item, .mirror, .sv a, .server a").forEach { element ->
            val name = element.text().trim()
            val url = element.attr("abs:href")
            if (name.isNotEmpty() && url.isNotEmpty()) {
                servers[name] = url
            }
        }

        return servers
    }
}
