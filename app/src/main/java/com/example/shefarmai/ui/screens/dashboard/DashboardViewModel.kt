package com.example.shefarmai.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.FinancialSummary
import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.repository.FinanceRepository
import com.example.shefarmai.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val summary: FinancialSummary = FinancialSummary(),
    val recentListings: List<ProductListing> = emptyList(),
    val recentTransactions: List<FinancialRecord> = emptyList(),
    val activeListingCount: Int = 0,
    val isLoading: Boolean = false
)

class DashboardViewModel(
    private val marketplaceRepository: MarketplaceRepository,
    private val financeRepository: FinanceRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            marketplaceRepository.seedSampleDataIfEmpty()
            financeRepository.seedSampleDataIfEmpty()
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        financeRepository.getSummary(),
        marketplaceRepository.getAllListings(),
        financeRepository.getAllRecords()
    ) { summary, listings, records ->
        DashboardUiState(
            summary = summary,
            recentListings = listings.take(3),
            recentTransactions = records.take(3),
            activeListingCount = listings.count { it.status == "AVAILABLE" },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    class Factory(
        private val marketplaceRepository: MarketplaceRepository,
        private val financeRepository: FinanceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(marketplaceRepository, financeRepository) as T
        }
    }
}
