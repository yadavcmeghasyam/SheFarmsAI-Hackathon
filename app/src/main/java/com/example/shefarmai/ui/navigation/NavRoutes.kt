package com.example.shefarmai.ui.navigation

import androidx.compose.material.icons.Icons
import com.example.shefarmai.ui.icons.filled.AccountBalanceWallet
import com.example.shefarmai.ui.icons.filled.Dashboard
import com.example.shefarmai.ui.icons.automirrored.filled.MenuBook
import com.example.shefarmai.ui.icons.filled.Psychology
import com.example.shefarmai.ui.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : NavRoute("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Marketplace : NavRoute("marketplace", "Marketplace", Icons.Default.Storefront)
    object Finance : NavRoute("finance", "Finances", Icons.Default.AccountBalanceWallet)
    object Assistant : NavRoute("assistant", "AI Assistant", Icons.Default.Psychology)
    object Guidance : NavRoute("guidance", "Guides", Icons.AutoMirrored.Filled.MenuBook)

    companion object {
        val bottomNavItems = listOf(Dashboard, Marketplace, Finance, Assistant, Guidance)
    }
}
