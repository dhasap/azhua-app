package com.azhua.ext.anichin

import android.app.Activity
import android.os.Bundle

/**
 * ExtensionMarkerActivity - Pemancar Sinyal Deteksi
 * 
 * Activity kosong ini berfungsi sebagai "marker" agar Core App (AzHua)
 * bisa mendeteksi keberadaan ekstensi ini melalui PackageManager.
 * 
 * Dengan adanya activity ini di Manifest dengan intent-filter
 * azhua.intent.action.EXTENSION, Core App bisa query:
 *   queryIntentActivities(Intent("azhua.intent.action.EXTENSION"), ...)
 * 
 * Ini adalah solusi untuk Package Visibility restrictions di Android 11+.
 */
class ExtensionMarkerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Activity ini tidak menampilkan UI apapun
        // Hanya bertugas sebagai "beacon" untuk deteksi
        finish()
    }
}
