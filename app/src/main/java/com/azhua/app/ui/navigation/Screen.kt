package com.azhua.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class untuk mendefinisikan route navigasi aplikasi AzHua
 * Digunakan oleh Bottom Navigation dan Navigation Graph
 */
sealed class Screen(
    val route: String,
    val title: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
) {
    /**
     * Pusaka - Koleksi Donghua pengguna
     * Berisi daftar anime yang sedang ditonton dan favorit
     */
    object Library : Screen(
        route = "library",
        title = "Pusaka",
        iconSelected = Icons.Filled.LocalLibrary,
        iconUnselected = Icons.Outlined.LocalLibrary
    )

    /**
     * Jelajah - Katalog dan pencarian Donghua
     * Menjelajahi berbagai sumber anime dari ekstensi
     */
    object Discover : Screen(
        route = "discover",
        title = "Jelajah",
        iconSelected = Icons.Filled.Explore,
        iconUnselected = Icons.Outlined.Explore
    )

    /**
     * Riwayat - History tontonan pengguna
     * Episode yang baru ditonton dan progress tontonan
     */
    object History : Screen(
        route = "history",
        title = "Riwayat",
        iconSelected = Icons.Filled.History,
        iconUnselected = Icons.Outlined.History
    )

    /**
     * Paviliun - Repository dan manajemen ekstensi
     * Mengelola sumber Donghua (install/uninstall/update)
     */
    object Extensions : Screen(
        route = "extensions",
        title = "Paviliun",
        iconSelected = Icons.Filled.Extension,
        iconUnselected = Icons.Outlined.Extension
    )

    companion object {
        /**
         * Daftar semua screen untuk Bottom Navigation
         */
        val bottomNavItems = listOf(Library, Discover, History, Extensions)

        /**
         * Mendapatkan Screen berdasarkan route string
         */
        fun fromRoute(route: String?): Screen {
            return when (route) {
                Library.route -> Library
                Discover.route -> Discover
                History.route -> History
                Extensions.route -> Extensions
                else -> Library // Default
            }
        }
    }
}
