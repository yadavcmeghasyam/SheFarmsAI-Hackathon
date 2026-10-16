package com.example.shefarmai.ui.screens.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MarketplaceUiState(
    val listings: List<ProductListing> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MarketplaceViewModel(
    private val repository: MarketplaceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")
    private val _errorMessage = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    val uiState: StateFlow<MarketplaceUiState> = combine(
        repository.getAllListings(),
        _searchQuery,
        _selectedCategory,
        _errorMessage
    ) { listings, query, category, error ->
        val filtered = listings.filter { listing ->
            val matchesCategory = category == "All" || listing.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    listing.title.contains(query, ignoreCase = true) ||
                    listing.location.contains(query, ignoreCase = true) ||
                    listing.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        MarketplaceUiState(
            listings = filtered,
            searchQuery = query,
            selectedCategory = category,
            isLoading = false,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MarketplaceUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun saveListing(
        id: Long = 0,
        title: String,
        category: String,
        pricePerUnit: Double?,
        unit: String,
        quantity: Double?,
        location: String,
        contact: String,
        description: String
    ): Boolean {
        if (title.isBlank()) {
            _errorMessage.value = "Product title cannot be empty"
            return false
        }
        if (pricePerUnit == null || pricePerUnit <= 0.0) {
            _errorMessage.value = "Please enter a valid price per unit"
            return false
        }
        if (quantity == null || quantity <= 0.0) {
            _errorMessage.value = "Please enter a valid quantity"
            return false
        }
        if (location.isBlank()) {
            _errorMessage.value = "Location cannot be empty"
            return false
        }
        if (contact.isBlank()) {
            _errorMessage.value = "Contact phone or info cannot be empty"
            return false
        }

        val listing = ProductListing(
            id = id,
            title = title.trim(),
            category = if (category.isBlank()) "General" else category.trim(),
            pricePerUnit = pricePerUnit,
            unit = if (unit.isBlank()) "unit" else unit.trim(),
            availableQuantity = quantity,
            location = location.trim(),
            contactContactInfo = contact.trim(),
            description = description.trim(),
            status = "AVAILABLE"
        )

        viewModelScope.launch {
            if (id == 0L) {
                repository.addListing(listing)
            } else {
                repository.updateListing(listing)
            }
        }
        _errorMessage.value = null
        return true
    }

    fun deleteListing(listing: ProductListing) {
        viewModelScope.launch {
            repository.deleteListing(listing)
        }
    }

    class Factory(
        private val repository: MarketplaceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MarketplaceViewModel(repository) as T
        }
    }
}
