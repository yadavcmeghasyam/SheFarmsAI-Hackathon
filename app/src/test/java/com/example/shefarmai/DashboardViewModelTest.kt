package com.example.shefarmai

import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.FinancialSummary
import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.model.TransactionType
import com.example.shefarmai.data.repository.FinanceRepository
import com.example.shefarmai.data.repository.FinanceRepositoryImpl
import com.example.shefarmai.data.repository.MarketplaceRepository
import com.example.shefarmai.ui.screens.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockMarketplaceRepository: FakeMarketplaceRepository
    private lateinit var mockFinanceRepository: FakeFinanceRepository
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockMarketplaceRepository = FakeMarketplaceRepository()
        mockFinanceRepository = FakeFinanceRepository()
        viewModel = DashboardViewModel(mockMarketplaceRepository, mockFinanceRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_aggregatesActiveListingsAndRecentRecords() = runTest {
        mockMarketplaceRepository.addListing(
            ProductListing(id = 1, title = "Maize", category = "Grains", pricePerUnit = 2800.0, unit = "bag", availableQuantity = 10.0, location = "Eldoret", contactContactInfo = "0712", description = "", status = "AVAILABLE")
        )
        mockMarketplaceRepository.addListing(
            ProductListing(id = 2, title = "Tomatoes", category = "Vegetables", pricePerUnit = 90.0, unit = "kg", availableQuantity = 50.0, location = "Nakuru", contactContactInfo = "0723", description = "", status = "SOLD")
        )

        mockFinanceRepository.addRecord(
            FinancialRecord(id = 1, type = TransactionType.REVENUE, category = "Crop Sales", amount = 15000.0, description = "Maize")
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.activeListingCount)
        assertEquals(2, state.recentListings.size)
        assertEquals(1, state.recentTransactions.size)
    }

    private class FakeMarketplaceRepository : MarketplaceRepository {
        private val listings = MutableStateFlow<List<ProductListing>>(emptyList())

        override fun getAllListings(): Flow<List<ProductListing>> = listings

        override suspend fun getListingById(id: Long): ProductListing? = listings.value.find { it.id == id }

        override suspend fun addListing(listing: ProductListing): Long {
            val newList = listings.value.toMutableList()
            newList.add(listing)
            listings.value = newList
            return listing.id
        }

        override suspend fun updateListing(listing: ProductListing) {}
        override suspend fun deleteListing(listing: ProductListing) {}
        override suspend fun deleteListingById(id: Long) {}
        override suspend fun seedSampleDataIfEmpty() {}
    }

    private class FakeFinanceRepository : FinanceRepository {
        private val records = MutableStateFlow<List<FinancialRecord>>(emptyList())

        override fun getAllRecords(): Flow<List<FinancialRecord>> = records

        override fun getSummary(): Flow<FinancialSummary> {
            return records.map { FinanceRepositoryImpl.calculateSummary(it) }
        }

        override suspend fun getRecordById(id: Long): FinancialRecord? = records.value.find { it.id == id }

        override suspend fun addRecord(record: FinancialRecord): Long {
            val newList = records.value.toMutableList()
            newList.add(record)
            records.value = newList
            return record.id
        }

        override suspend fun updateRecord(record: FinancialRecord) {}
        override suspend fun deleteRecord(record: FinancialRecord) {}
        override suspend fun deleteRecordById(id: Long) {}
        override suspend fun seedSampleDataIfEmpty() {}
    }
}
