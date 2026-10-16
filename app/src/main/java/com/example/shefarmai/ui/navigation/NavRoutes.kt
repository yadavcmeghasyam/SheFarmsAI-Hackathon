package com.example.shefarmai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : NavRoute("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Marketplace : NavRoute("marketplace", "Marketplace", Icons.Default.Storefront)
    object Finance : NavRoute("finance", "Finances", Icons.Default.AccountBalanceWallet)
    object Assistant : NavRoute("assistant", "AI Assistant", Icons.Default.Psychology)
    object Guidance : NavRoute("guidance", "Guides", Icons.Default.MenuBook)

    companion object {
        val bottomNavItems = listOf(Dashboard, Marketplace, Finance, Assistant, Guidance)
    }
}
