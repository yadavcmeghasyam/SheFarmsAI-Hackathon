package com.example.shefarmai

import com.example.shefarmai.data.model.ProductStatus
import com.example.shefarmai.ui.screens.marketplace.MarketplaceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun editPreservesDateAndStatusUnlessChanged() = runTest {
        val repo = FakeMarketplaceRepository().apply { listings.value = listOf(sampleListing(status = ProductStatus.RESERVED)) }
        val vm = MarketplaceViewModel(repo)
        assertTrue(vm.saveListing(1, "Edited", "Grains", 200.0, "kg", 10.0, "Eldoret", "0700", ""))
        runCurrent()
        assertEquals(1000L, repo.listings.value.single().datePostedInMillis)
        assertEquals(ProductStatus.RESERVED, repo.listings.value.single().status)
        vm.saveListing(1, "Edited", "Grains", 200.0, "kg", 10.0, "Eldoret", "0700", "", status = ProductStatus.SOLD)
        runCurrent()
        assertEquals(ProductStatus.SOLD, repo.listings.value.single().status)
    }

    @Test fun createSearchCategoryAndDelete() = runTest {
        val repo = FakeMarketplaceRepository()
        val vm = MarketplaceViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        vm.saveListing(title = "Maize", category = "Grains", pricePerUnit = 100.0, unit = "kg", quantity = 5.0, location = "Eldoret", contact = "0700", description = "")
        runCurrent()
        assertEquals(1, vm.uiState.value.listings.size)
        vm.onSearchQueryChanged("missing")
        runCurrent()
        assertTrue(vm.uiState.value.listings.isEmpty())
        vm.onSearchQueryChanged("Eldoret")
        vm.onCategorySelected("Vegetables")
        runCurrent()
        assertTrue(vm.uiState.value.listings.isEmpty())
        vm.onCategorySelected("Grains")
        runCurrent()
        assertEquals(1, vm.uiState.value.listings.size)
        vm.deleteListing(repo.listings.value.single())
        runCurrent()
        assertTrue(repo.listings.value.isEmpty())
        assertEquals(0, repo.seedCalls)
    }

    @Test fun rejectsInvalidNumbersAndDoesNotCloseOnFailure() = runTest {
        val repo = FakeMarketplaceRepository()
        val vm = MarketplaceViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        for (number in listOf(null, 0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            assertFalse(vm.saveListing(title = "Maize", category = "Grains", pricePerUnit = number, unit = "kg", quantity = 5.0, location = "Eldoret", contact = "0700", description = ""))
            assertFalse(vm.saveListing(title = "Maize", category = "Grains", pricePerUnit = 100.0, unit = "kg", quantity = number, location = "Eldoret", contact = "0700", description = ""))
        }
        repo.failWrites = true
        var closed = false
        vm.saveListing(title = "Maize", category = "Grains", pricePerUnit = 100.0, unit = "kg", quantity = 5.0, location = "Eldoret", contact = "0700", description = "", onSaved = { closed = true })
        runCurrent()
        assertFalse(closed)
        assertFalse(vm.uiState.value.isSaving)
        assertNotNull(vm.uiState.value.errorMessage)
    }
}
