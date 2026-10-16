package com.example.shefarmai.ui.screens.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.FinancialSummary
import com.example.shefarmai.data.model.TransactionType
import com.example.shefarmai.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

data class FinanceUiState(
    val records: List<FinancialRecord> = emptyList(),
    val summary: FinancialSummary = FinancialSummary(),
    val selectedFilter: String = "ALL", // ALL, REVENUE, EXPENSE
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("ALL")
    private val _isSaving = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)


    val uiState: StateFlow<FinanceUiState> = combine(
        repository.getAllRecords(),
        repository.getSummary(),
        _selectedFilter,
        _errorMessage,
        _isSaving
    ) { records, summary, filter, error, saving ->
        val filtered = when (filter) {
            "REVENUE" -> records.filter { it.type == TransactionType.REVENUE }
            "EXPENSE" -> records.filter { it.type == TransactionType.EXPENSE }
            else -> records
        }

        FinanceUiState(
            records = filtered,
            summary = summary,
            selectedFilter = filter,
            isLoading = false,
            isSaving = saving,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinanceUiState(isLoading = true)
    )

    fun onFilterSelected(filter: String) {
        _selectedFilter.value = filter
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun saveRecord(
        id: Long = 0,
        type: TransactionType,
        category: String,
        amount: Double?,
        description: String,
        onSaved: () -> Unit = {}
    ): Boolean {
        if (_isSaving.value) return false
        if (amount == null || !amount.isFinite() || amount <= 0.0) {
            _errorMessage.value = "Please enter a valid amount greater than 0"
            return false
        }
        if (category.isBlank()) {
            _errorMessage.value = "Category cannot be empty"
            return false
        }

        val record = FinancialRecord(
            id = id,
            type = type,
            category = category.trim(),
            amount = amount,
            description = description.trim(),
            dateInMillis = System.currentTimeMillis()
        )

        _isSaving.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                if (id == 0L) {
                    repository.addRecord(record)
                } else {
                    val original = repository.getRecordById(id) ?: error("Transaction no longer exists")
                    repository.updateRecord(record.copy(dateInMillis = original.dateInMillis))
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

    fun deleteRecord(record: FinancialRecord) {
        viewModelScope.launch {
            try {
                repository.deleteRecord(record)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _errorMessage.value = "Could not save changes. Please try again."
            }
        }
    }

    class Factory(
        private val repository: FinanceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FinanceViewModel(repository) as T
        }
    }
}
