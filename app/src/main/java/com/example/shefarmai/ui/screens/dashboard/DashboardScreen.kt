package com.example.shefarmai.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shefarmai.ui.components.FinancialRecordItem
import com.example.shefarmai.ui.components.FinancialSummaryCard
import com.example.shefarmai.ui.components.ProductListingCard
import com.example.shefarmai.ui.navigation.NavRoute

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigate: (NavRoute) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "🌾 SheFarm AI Dashboard",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Empowering Women in Agriculture with On-Device AI & Smart Farm Financial Management",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                    )
                }
            }

            // Financial P&L Card
            FinancialSummaryCard(summary = uiState.summary)

            // Quick Actions Bar
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onNavigate(NavRoute.Marketplace) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text("New Listing")
                }

                Button(
                    onClick = { onNavigate(NavRoute.Finance) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text("Record P&L")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(NavRoute.Assistant) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text("Ask AI Assistant")
                }

                OutlinedButton(
                    onClick = { onNavigate(NavRoute.Guidance) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Storefront, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text("Farming Guides")
                }
            }

            // Recent Marketplace Listings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Recent Produce Listings (${uiState.activeListingCount} Active)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.recentListings.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No produce listings yet. Click 'New Listing' above to add your harvest!",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                uiState.recentListings.forEach { listing ->
                    ProductListingCard(
                        listing = listing,
                        onEditClick = { onNavigate(NavRoute.Marketplace) },
                        onDeleteClick = { onNavigate(NavRoute.Marketplace) }
                    )
                }
            }

            // Recent Financial Transactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Financial Records",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.recentTransactions.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No financial records entered yet. Log your expenses and revenue to track profit!",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                uiState.recentTransactions.forEach { record ->
                    FinancialRecordItem(
                        record = record,
                        onEditClick = { onNavigate(NavRoute.Finance) },
                        onDeleteClick = { onNavigate(NavRoute.Finance) }
                    )
                }
            }
        }
    }
}
