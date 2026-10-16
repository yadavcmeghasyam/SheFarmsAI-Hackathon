package com.example.shefarmai.ui.screens.guidance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shefarmai.data.model.FarmingGuide
import com.example.shefarmai.data.repository.GuidanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class GuidanceUiState(
    val guides: List<FarmingGuide> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val selectedGuide: FarmingGuide? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class GuidanceViewModel(
    private val repository: GuidanceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")
    private val _selectedGuide = MutableStateFlow<FarmingGuide?>(null)

    val uiState: StateFlow<GuidanceUiState> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        repository.searchGuides(query, category)
    }.combine(_searchQuery) { guides, query ->
        Pair(guides, query)
    }.combine(_selectedCategory) { (guides, query), category ->
        Triple(guides, query, category)
    }.combine(_selectedGuide) { (guides, query, category), selectedGuide ->
        GuidanceUiState(
            guides = guides,
            searchQuery = query,
            selectedCategory = category,
            selectedGuide = selectedGuide
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GuidanceUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun selectGuide(guide: FarmingGuide?) {
        _selectedGuide.value = guide
    }

    class Factory(
        private val repository: GuidanceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GuidanceViewModel(repository) as T
        }
    }
}
