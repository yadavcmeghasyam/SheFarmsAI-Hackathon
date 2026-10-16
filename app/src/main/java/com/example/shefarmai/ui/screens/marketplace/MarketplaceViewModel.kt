package com.example.shefarmai.ui.screens.marketplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.model.ProductStatus
import com.example.shefarmai.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

data class MarketplaceUiState(
    val listings: List<ProductListing> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class MarketplaceViewModel(
    private val repository: MarketplaceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")
    private val _isSaving = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)


    val uiState: StateFlow<MarketplaceUiState> = combine(
        repository.getAllListings(),
        _searchQuery,
        _selectedCategory,
        _errorMessage,
        _isSaving
    ) { listings, query, category, error, saving ->
        val normalizedQuery = query.trim()
        val filtered = listings.filter { listing ->
            val matchesCategory = category == "All" || listing.category.equals(category, ignoreCase = true)
            val matchesQuery = normalizedQuery.isBlank() ||
                    listing.title.contains(normalizedQuery, ignoreCase = true) ||
                    listing.location.contains(normalizedQuery, ignoreCase = true) ||
                    listing.description.contains(normalizedQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        MarketplaceUiState(
            listings = filtered,
            searchQuery = query,
            selectedCategory = category,
            isLoading = false,
            isSaving = saving,
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
        description: String,
        status: ProductStatus? = null,
        onSaved: () -> Unit = {}
    ): Boolean {
        if (_isSaving.value) return false
        if (title.isBlank()) {
            _errorMessage.value = "Product title cannot be empty"
            return false
        }
        if (pricePerUnit == null || !pricePerUnit.isFinite() || pricePerUnit <= 0.0) {
            _errorMessage.value = "Please enter a valid price per unit"
            return false
        }
        if (quantity == null || !quantity.isFinite() || quantity <= 0.0) {
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
            contactInfo = contact.trim(),
            description = description.trim(),
            status = status ?: ProductStatus.AVAILABLE
        )

        _isSaving.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                if (id == 0L) {
                    repository.addListing(listing)
                } else {
                    val original = repository.getListingById(id) ?: error("Listing no longer exists")
                    repository.updateListing(listing.copy(datePostedInMillis = original.datePostedInMillis, status = status ?: original.status))
                }
                onSaved()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value = "Could not save changes. Please try again."
            } finally {
                _isSaving.value = false
            }
        }
        return true
    }

    fun deleteListing(listing: ProductListing) {
        viewModelScope.launch {
            try {
                repository.deleteListing(listing)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value = "Could not save changes. Please try again."
            }
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
