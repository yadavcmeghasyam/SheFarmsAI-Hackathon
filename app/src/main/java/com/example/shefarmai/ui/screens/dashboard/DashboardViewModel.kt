package com.example.shefarmai.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.FinancialSummary
import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.model.ProductStatus
import com.example.shefarmai.data.repository.FinanceRepository
import com.example.shefarmai.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow

data class DashboardUiState(
    val summary: FinancialSummary = FinancialSummary(),
    val recentListings: List<ProductListing> = emptyList(),
    val recentTransactions: List<FinancialRecord> = emptyList(),
    val activeListingCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val marketplaceRepository: MarketplaceRepository,
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val error = MutableStateFlow<String?>(null)

    fun loadSampleData() {
        viewModelScope.launch {
            try {
                marketplaceRepository.seedSampleDataIfEmpty()
                financeRepository.seedSampleDataIfEmpty()
                error.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = "Could not load sample data. Please try again."
            }
        }
    }

    fun deleteListing(listing: ProductListing) {
        viewModelScope.launch {
            try {
                marketplaceRepository.deleteListing(listing)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = "Could not delete listing. Please try again."
            }
        }
    }

    fun deleteRecord(record: FinancialRecord) {
        viewModelScope.launch {
            try {
                financeRepository.deleteRecord(record)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = "Could not delete record. Please try again."
            }
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        financeRepository.getSummary(),
        marketplaceRepository.getAllListings(),
        financeRepository.getAllRecords(),
        error
    ) { summary, listings, records, errorMessage ->
        DashboardUiState(
            summary = summary,
            recentListings = listings.take(3),
            recentTransactions = records.take(3),
            activeListingCount = listings.count { it.status == ProductStatus.AVAILABLE },
            isLoading = false,
            errorMessage = errorMessage
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
