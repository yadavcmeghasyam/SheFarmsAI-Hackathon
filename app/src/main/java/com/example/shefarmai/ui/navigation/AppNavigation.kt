package com.example.shefarmai.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shefarmai.data.local.SheFarmDatabase
import com.example.shefarmai.data.local.LocalChatHistoryStore
import com.example.shefarmai.data.local.RoomChatHistoryStore
import androidx.compose.foundation.layout.imePadding
import com.example.shefarmai.data.repository.AssistantRepositoryImpl
import com.example.shefarmai.data.repository.AssistantRepository
import com.example.shefarmai.data.repository.FinanceRepositoryImpl
import com.example.shefarmai.data.repository.GuidanceRepositoryImpl
import com.example.shefarmai.data.repository.MarketplaceRepositoryImpl
import com.example.shefarmai.ui.components.SheFarmBottomBar
import com.example.shefarmai.ui.screens.assistant.AssistantScreen
import com.example.shefarmai.ui.screens.assistant.AssistantViewModel
import com.example.shefarmai.ui.screens.dashboard.DashboardScreen
import com.example.shefarmai.ui.screens.dashboard.DashboardViewModel
import com.example.shefarmai.ui.screens.finance.FinanceScreen
import com.example.shefarmai.ui.screens.finance.FinanceViewModel
import com.example.shefarmai.ui.screens.guidance.GuidanceScreen
import com.example.shefarmai.ui.screens.guidance.GuidanceViewModel
import com.example.shefarmai.ui.screens.marketplace.MarketplaceScreen
import com.example.shefarmai.ui.screens.marketplace.MarketplaceViewModel

@Composable
fun AppNavigation(
    databaseOverride: SheFarmDatabase? = null,
    assistantRepositoryOverride: AssistantRepository? = null
) {
    val context = LocalContext.current.applicationContext
    val database = remember(databaseOverride) { databaseOverride ?: SheFarmDatabase.getDatabase(context) }

    val marketplaceRepository = remember { MarketplaceRepositoryImpl(database.productListingDao()) }
    val financeRepository = remember { FinanceRepositoryImpl(database.financialRecordDao()) }
    val assistantRepository = remember(assistantRepositoryOverride) {
        assistantRepositoryOverride ?: AssistantRepositoryImpl(
            RoomChatHistoryStore(
                chatMessageDao = database.chatMessageDao(),
                legacyStore = LocalChatHistoryStore(context)
            )
        )
    }
    val guidanceRepository = remember { GuidanceRepositoryImpl() }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.imePadding(),
        bottomBar = {
            SheFarmBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (currentRoute != route.route) {
                        navController.navigate(route.route) {
                            popUpTo(NavRoute.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Dashboard.route) {
                val dashboardViewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.Factory(marketplaceRepository, financeRepository)
                )
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigate = { route ->
                        navController.navigate(route.route) {
                            popUpTo(NavRoute.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(NavRoute.Marketplace.route) {
                val marketplaceViewModel: MarketplaceViewModel = viewModel(
                    factory = MarketplaceViewModel.Factory(marketplaceRepository)
                )
                MarketplaceScreen(viewModel = marketplaceViewModel)
            }

            composable(NavRoute.Finance.route) {
                val financeViewModel: FinanceViewModel = viewModel(
                    factory = FinanceViewModel.Factory(financeRepository)
                )
                FinanceScreen(viewModel = financeViewModel)
            }

            composable(NavRoute.Assistant.route) {
                val assistantViewModel: AssistantViewModel = viewModel(
                    factory = AssistantViewModel.Factory(assistantRepository)
                )
                AssistantScreen(viewModel = assistantViewModel)
            }

            composable(NavRoute.Guidance.route) {
                val guidanceViewModel: GuidanceViewModel = viewModel(
                    factory = GuidanceViewModel.Factory(guidanceRepository)
                )
                GuidanceScreen(viewModel = guidanceViewModel)
            }
        }
    }
}
