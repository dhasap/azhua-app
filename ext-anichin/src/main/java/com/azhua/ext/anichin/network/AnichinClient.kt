package com.azhua.ext.anichin.network

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.concurrent.TimeUnit

/**
 * AnichinClient - Penjelajah Alam Maya
 *
 * HTTP Client khusus untuk mengakses anichin.co.id
 * Dilengkapi dengan User-Agent spoofing dan timeout handling.
 */
class AnichinClient {

    companion object {
        const val BASE_URL = "https://anichin.co.id"
        const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        const val TIMEOUT_SECONDS = 30L
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    /**
     * Melakukan HTTP GET dan mengembalikan Document Jsoup
     */
    fun getDocument(url: String): Document? {
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .header("Cache-Control", "max-age=0")
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body?.string()
                if (body != null) {
                    Jsoup.parse(body, url)
                } else null
            } else {
                println("AnichinClient: HTTP Error ${response.code}")
                null
            }
        } catch (e: Exception) {
            println("AnichinClient: Error fetching $url - ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * GET dengan Jsoup langsung (alternatif)
     */
    fun getDocumentWithJsoup(url: String): Document? {
        return try {
            Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout((TIMEOUT_SECONDS * 1000).toInt())
                .followRedirects(true)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .get()
        } catch (e: Exception) {
            println("AnichinClient Jsoup: Error - ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * POST request untuk form (misal: search)
     */
    fun postDocument(url: String, formData: Map<String, String>): Document? {
        return try {
            val formBody = okhttp3.FormBody.Builder().apply {
                formData.forEach { (key, value) ->
                    add(key, value)
                }
            }.build()

            val request = Request.Builder()
                .url(url)
                .post(formBody)
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", BASE_URL)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val body = response.body?.string()
                if (body != null) Jsoup.parse(body, url) else null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Resolve URL relatif menjadi absolut
     */
    fun resolveUrl(relativeUrl: String): String {
        return when {
            relativeUrl.startsWith("http") -> relativeUrl
            relativeUrl.startsWith("/") -> BASE_URL + relativeUrl
            else -> "$BASE_URL/$relativeUrl"
        }
    }
}
