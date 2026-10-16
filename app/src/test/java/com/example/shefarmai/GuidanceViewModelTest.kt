package com.example.shefarmai

import com.example.shefarmai.data.model.FarmingGuide
import com.example.shefarmai.data.repository.GuidanceRepository
import com.example.shefarmai.ui.screens.guidance.GuidanceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GuidanceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: FakeGuidanceRepository
    private lateinit var viewModel: GuidanceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = FakeGuidanceRepository()
        viewModel = GuidanceViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectGuide_updatesSelectedGuideState() {
        assertNull(viewModel.uiState.value.selectedGuide)

        val testGuide = FarmingGuide(
            id = "guide_1",
            title = "Pest Control Test",
            category = "Pest Control",
            summary = "Summary",
            detailContent = "Detail",
            actionableTips = listOf("Tip 1"),
            suitableCrops = listOf("Maize")
        )

        viewModel.selectGuide(testGuide)
        assertEquals(testGuide, viewModel.uiState.value.selectedGuide)

        viewModel.selectGuide(null)
        assertNull(viewModel.uiState.value.selectedGuide)
    }

    @Test
    fun onCategorySelected_updatesCategory() {
        viewModel.onCategorySelected("Soil Health")
        assertEquals("Soil Health", viewModel.uiState.value.selectedCategory)
    }

    @Test
    fun onSearchQueryChanged_updatesSearchQuery() {
        viewModel.onSearchQueryChanged("compost")
        assertEquals("compost", viewModel.uiState.value.searchQuery)
    }

    private class FakeGuidanceRepository : GuidanceRepository {
        private val sampleGuides = listOf(
            FarmingGuide(
                id = "guide_1",
                title = "Fall Armyworm",
                category = "Pest Control",
                summary = "Pest guide",
                detailContent = "Details",
                actionableTips = listOf("Tip 1"),
                suitableCrops = listOf("Maize")
            ),
            FarmingGuide(
                id = "guide_2",
                title = "Organic Composting",
                category = "Soil Health",
                summary = "Compost guide",
                detailContent = "Details",
                actionableTips = listOf("Tip 2"),
                suitableCrops = listOf("Vegetables")
            )
        )

        override fun getGuides(): Flow<List<FarmingGuide>> = flowOf(sampleGuides)

        override fun getGuideById(id: String): FarmingGuide? = sampleGuides.find { it.id == id }

        override fun searchGuides(query: String, category: String?): Flow<List<FarmingGuide>> {
            val filtered = sampleGuides.filter { guide ->
                val matchesCategory = category.isNullOrBlank() || category == "All" || guide.category.equals(category, ignoreCase = true)
                val matchesQuery = query.isBlank() || guide.title.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
            return flowOf(filtered)
        }
    }
}
