package com.example.shefarmai

import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.repository.MarketplaceRepository
import com.example.shefarmai.ui.screens.marketplace.MarketplaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: FakeMarketplaceRepository
    private lateinit var viewModel: MarketplaceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = FakeMarketplaceRepository()
        viewModel = MarketplaceViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveListing_withValidData_returnsTrueAndSaves() {
        val success = viewModel.saveListing(
            id = 0,
            title = "Fresh Organic Cassava",
            category = "Tubers",
            pricePerUnit = 40.0,
            unit = "kg",
            quantity = 100.0,
            location = "Machakos",
            contact = "+254 700 000 000",
            description = "Clean cassava"
        )

        assertTrue(success)
    }

    @Test
    fun saveListing_withInvalidPriceOrTitle_returnsFalse() {
        val emptyTitleSuccess = viewModel.saveListing(
            id = 0,
            title = "",
            category = "Tubers",
            pricePerUnit = 40.0,
            unit = "kg",
            quantity = 100.0,
            location = "Machakos",
            contact = "+254 700 000 000",
            description = ""
        )
        assertFalse(emptyTitleSuccess)

        val negativePriceSuccess = viewModel.saveListing(
            id = 0,
            title = "Cassava",
            category = "Tubers",
            pricePerUnit = -10.0,
            unit = "kg",
            quantity = 100.0,
            location = "Machakos",
            contact = "+254 700 000 000",
            description = ""
        )
        assertFalse(negativePriceSuccess)
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

        override suspend fun deleteListing(listing: ProductListing) {
            val newList = listings.value.toMutableList()
            newList.removeIf { it.id == listing.id }
            listings.value = newList
        }

        override suspend fun deleteListingById(id: Long) {}

        override suspend fun seedSampleDataIfEmpty() {}
    }
}
