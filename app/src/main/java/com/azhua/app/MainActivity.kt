package com.azhua.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.azhua.app.ui.screens.MainScreen
import com.azhua.app.ui.theme.AzHuaTheme

/**
 * MainActivity - Entry point aplikasi AzHua
 * 
 * Aplikasi streaming Donghua dengan arsitektur modular dan sistem ekstensi.
 * 
 * Fitur utama:
 * - 🏛️ Paviliun Kitab: Repository ekstensi untuk sumber Donghua
 * - 📚 Pusaka: Koleksi dan library pengguna
 * - 🔍 Jelajah: Katalog dan pencarian
 * - ⏱️ Riwayat: History tontonan
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge untuk tampilan imersif
        enableEdgeToEdge()
        
        setContent {
            AzHuaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Gerbang Utama Sekte dengan Bottom Navigation
                    MainScreen()
                }
            }
        }
    }
}
