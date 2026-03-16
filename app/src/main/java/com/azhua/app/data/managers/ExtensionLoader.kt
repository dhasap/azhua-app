package com.azhua.app.data.managers

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.azhua.core.contracts.Source
import dalvik.system.PathClassLoader

/**
 * ExtensionLoader - Master teknik PathClassLoader
 * 
 * Kelas ini bertanggung jawab untuk:
 * 1. Memindai semua aplikasi terinstal di HP pengguna
 * 2. Mendeteksi APK yang memiliki meta-data "azhua.extension.class"
 * 3. Memuat (load) class Source dari APK ekstensi tersebut
 * 4. Mengembalikan instance Source yang bisa digunakan oleh Core App
 * 
 * Mekanisme ini memungkinkan Core App memanggil kode dari APK terpisah
 * tanpa menggunakan IPC (Inter-Process Communication) yang berat.
 */
class ExtensionLoader(private val context: Context) {

    companion object {
        private const val TAG = "AzHua_ExtensionLoader"
        private const val META_DATA_KEY = "azhua.extension.class"
    }

    private val loadedExtensions = mutableListOf<Source>()

    /**
     * Memuat semua ekstensi yang terinstal di perangkat.
     * 
     * @return List of Source instances dari ekstensi yang berhasil dimuat
     */
    fun loadInstalledExtensions(): List<Source> {
        loadedExtensions.clear()
        val packageManager = context.packageManager
        
        // Dapatkan semua aplikasi terinstal yang membawa META-DATA
        val installedPackages = packageManager.getInstalledPackages(
            PackageManager.GET_META_DATA or PackageManager.GET_SHARED_LIBRARY_FILES
        )

        Log.d(TAG, "Memindai ${installedPackages.size} aplikasi terinstal...")

        for (packageInfo in installedPackages) {
            val appInfo = packageInfo.applicationInfo ?: continue
            val metaData = appInfo.metaData ?: continue

            // Periksa apakah ini APK Ekstensi AzHua
            if (metaData.containsKey(META_DATA_KEY)) {
                val className = metaData.getString(META_DATA_KEY)
                val packageName = packageInfo.packageName

                Log.d(TAG, "Menemukan ekstensi: $packageName -> $className")

                try {
                    val sourceInstance = loadExtensionSource(packageName, className)
                    if (sourceInstance != null) {
                        loadedExtensions.add(sourceInstance)
                        Log.i(TAG, "✅ Berhasil memuat ekstensi: ${sourceInstance.name} (${sourceInstance.language})")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Gagal memuat ekstensi dari $packageName", e)
                }
            }
        }

        Log.d(TAG, "Total ekstensi berhasil dimuat: ${loadedExtensions.size}")
        return loadedExtensions.toList()
    }

    /**
     * Memuat satu ekstensi berdasarkan package name.
     * 
     * @param packageName Package name dari APK ekstensi
     * @param className Nama class lengkap yang mengimplementasikan Source
     * @return Instance Source atau null jika gagal
     */
    private fun loadExtensionSource(packageName: String, className: String?): Source? {
        if (className.isNullOrBlank()) {
            Log.w(TAG, "Class name tidak valid untuk $packageName")
            return null
        }

        // 1. Buat Context khusus dari APK Ekstensi
        val extContext = context.createPackageContext(
            packageName,
            Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
        )

        // 2. Dapatkan path ke APK file
        val apkPath = extContext.applicationInfo.sourceDir
            ?: throw IllegalStateException("Cannot get APK path for $packageName")

        // 3. Dapatkan native library path
        val nativeLibPath = extContext.applicationInfo.nativeLibraryDir

        Log.d(TAG, "APK Path: $apkPath")
        Log.d(TAG, "Native Lib Path: $nativeLibPath")

        // 4. Buat ClassLoader khusus untuk ekstensi ini
        // Parent classloader adalah classloader dari Core App
        val classLoader = PathClassLoader(
            apkPath,
            nativeLibPath,
            context.classLoader
        )

        // 5. Muat class Source dan instansiasi!
        @Suppress("DEPRECATION")
        val sourceClass = Class.forName(className, false, classLoader)
        
        // Pastikan class mengimplementasikan Source interface
        if (!Source::class.java.isAssignableFrom(sourceClass)) {
            throw ClassCastException("$className tidak mengimplementasikan interface Source")
        }

        // 6. Buat instance
        @Suppress("DEPRECATION")
        val sourceInstance = sourceClass.newInstance() as Source

        return sourceInstance
    }

    /**
     * Mendapatkan daftar ekstensi yang sudah dimuat sebelumnya.
     * Tidak melakukan scanning ulang.
     */
    fun getLoadedExtensions(): List<Source> {
        return loadedExtensions.toList()
    }

    /**
     * Cek apakah ada ekstensi dengan ID tertentu yang sudah dimuat.
     */
    fun hasExtension(extensionId: String): Boolean {
        return loadedExtensions.any { it.id == extensionId }
    }

    /**
     * Mendapatkan ekstensi berdasarkan ID.
     */
    fun getExtensionById(extensionId: String): Source? {
        return loadedExtensions.find { it.id == extensionId }
    }

    /**
     * Mendapatkan ekstensi berdasarkan bahasa.
     */
    fun getExtensionsByLanguage(lang: String): List<Source> {
        return loadedExtensions.filter { it.language.equals(lang, ignoreCase = true) }
    }

    /**
     * Reload semua ekstensi (scan ulang).
     */
    fun reloadExtensions(): List<Source> {
        return loadInstalledExtensions()
    }
}

/**
 * Exception khusus untuk error loading extension.
 */
class ExtensionLoadException(message: String, cause: Throwable? = null) : Exception(message, cause)
