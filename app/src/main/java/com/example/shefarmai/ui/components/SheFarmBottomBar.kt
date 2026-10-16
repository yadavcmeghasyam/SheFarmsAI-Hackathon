package com.example.shefarmai.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.example.shefarmai.ui.navigation.NavRoute

@Composable
fun SheFarmBottomBar(
    currentRoute: String?,
    onNavigate: (NavRoute) -> Unit
) {
    NavigationBar {
        NavRoute.bottomNavItems.forEach { navItem ->
            val isSelected = currentRoute == navItem.route
            NavigationBarItem(
                modifier = Modifier.semantics { contentDescription = navItem.title },
                selected = isSelected,
                onClick = { onNavigate(navItem) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = navItem.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
