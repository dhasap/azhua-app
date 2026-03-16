package com.azhua.app.data.managers

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.azhua.app.data.models.ExtensionItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * ExtensionManager - Jantung dari Paviliun Kitab
 * 
 * Mengelola komunikasi dengan repository GitHub untuk:
 * - Mengambil daftar ekstensi yang tersedia
 * - Mengunduh APK ekstensi
 * - Memicu instalasi ekstensi
 */
class ExtensionManager(private val context: Context) {

    private val client = OkHttpClient()
    private val gson = Gson()
    
    /**
     * URL Repository JSON - GANTI dengan URL GitHub Anda!
     * Format: https://raw.githubusercontent.com/USERNAME/azhua-extensions/main/index.json
     */
    private val repoUrl = "https://raw.githubusercontent.com/dhasap/azhua-extensions/main/index.json"
    
    /**
     * Callback untuk tracking download selesai
     */
    interface DownloadCallback {
        fun onDownloadComplete(extension: ExtensionItem, apkFile: File)
        fun onDownloadFailed(extension: ExtensionItem, error: String)
    }

    /**
     * Mengambil daftar ekstensi dari GitHub secara asynchronous
     * 
     * @return List<ExtensionItem> yang tersedia, atau empty list jika error
     */
    suspend fun fetchAvailableExtensions(): List<ExtensionItem> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(repoUrl)
                .header("Accept", "application/json")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val jsonStr = response.body?.string()
                if (!jsonStr.isNullOrBlank()) {
                    val listType = object : TypeToken<List<ExtensionItem>>() {}.type
                    return@withContext gson.fromJson<List<ExtensionItem>>(jsonStr, listType)
                        .sortedBy { it.name }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext emptyList()
    }

    /**
     * Mengunduh APK Ekstensi menggunakan DownloadManager bawaan Android
     * dengan Jurus Ranah Pribadi (ExternalFilesDir) untuk menghindari Force Close Android 11+
     * 
     * @param extension ExtensionItem yang akan diunduh
     * @return ID download untuk tracking
     */
    fun downloadExtensionApk(extension: ExtensionItem): Long {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        
        // Nama file yang bersih
        val fileName = "${extension.name}_${extension.versionName}.apk"
        
        val request = DownloadManager.Request(Uri.parse(extension.apkUrl))
            .setTitle("Mengunduh Ekstensi: ${extension.name}")
            .setDescription("Menyiapkan artefak untuk instalasi...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            // 🔥 JURUS RAHASIA: Simpan di Ranah Pribadi agar Android 11+ tidak Force Close!
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadId = downloadManager.enqueue(request)

        val onDownloadComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id) {
                    try {
                        // 1. Ambil file dari Ranah Pribadi
                        val apkFile = File(ctxt.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                        
                        if (apkFile.exists()) {
                            // 2. Gunakan FileProvider (Mantra dari Fase 2) agar disetujui oleh sistem!
                            val apkUri = FileProvider.getUriForFile(
                                ctxt,
                                "${ctxt.packageName}.provider",
                                apkFile
                            )
                            
                            // 3. Panggil Pop-up Instalasi!
                            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(apkUri, "application/vnd.android.package-archive")
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                            }
                            ctxt.startActivity(installIntent)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        // 4. Cabut mata-mata agar tidak terjadi kebocoran Qi (Memory Leak)
                        ctxt.unregisterReceiver(this)
                    }
                }
            }
        }

        // Daftarkan BroadcastReceiver menggunakan ApplicationContext agar stabil
        val appContext = context.applicationContext
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.registerReceiver(onDownloadComplete, filter, Context.RECEIVER_EXPORTED)
        } else {
            appContext.registerReceiver(onDownloadComplete, filter)
        }
        
        return downloadId
    }

    /**
     * Mendapatkan file APK dari download ID
     */
    fun getDownloadedApk(downloadId: Long): File? {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor = downloadManager.query(query)
        
        var file: File? = null
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                val uriString = cursor.getString(uriIndex)
                file = File(Uri.parse(uriString).path ?: "")
            }
        }
        cursor.close()
        return file
    }

    /**
     * Memicu antarmuka instalasi Android (Package Installer)
     * 
     * @param apkFile File APK yang akan diinstal
     */
    fun installApk(apkFile: File) {
        if (!apkFile.exists()) {
            throw IllegalArgumentException("File APK tidak ditemukan: ${apkFile.absolutePath}")
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        // Menggunakan FileProvider untuk API 24+ (Keamanan Android)
        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            apkFile
        )

        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * Cek apakah ekstensi sudah terinstal
     */
    fun isExtensionInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cek versi ekstensi yang terinstal
     */
    fun getInstalledVersion(packageName: String): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Membandingkan versi untuk update
     */
    fun needsUpdate(installedVersionCode: Int, newVersionCode: Int): Boolean {
        return newVersionCode > installedVersionCode
    }

    /**
     * Register BroadcastReceiver untuk tracking download selesai
     */
    fun registerDownloadReceiver(
        onComplete: (Long) -> Unit
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != -1L) {
                    onComplete(downloadId)
                }
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }
        
        return receiver
    }

    /**
     * Unregister BroadcastReceiver
     */
    fun unregisterDownloadReceiver(receiver: BroadcastReceiver) {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) {
            // Receiver sudah di-unregister
        }
    }

    companion object {
        const val TAG = "ExtensionManager"
    }
}

/**
 * Extension function untuk membantu sorting ekstensi
 */
fun List<ExtensionItem>.filterByLanguage(lang: String): List<ExtensionItem> {
    return this.filter { it.lang.equals(lang, ignoreCase = true) }
}

fun List<ExtensionItem>.filterInstalled(context: Context): List<ExtensionItem> {
    val manager = ExtensionManager(context)
    return this.filter { manager.isExtensionInstalled(it.pkg) }
}

fun List<ExtensionItem>.filterNotInstalled(context: Context): List<ExtensionItem> {
    val manager = ExtensionManager(context)
    return this.filter { !manager.isExtensionInstalled(it.pkg) }
}
