package com.example.shefarmai

import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.FinancialSummary
import com.example.shefarmai.data.model.TransactionType
import com.example.shefarmai.data.repository.FinanceRepository
import com.example.shefarmai.data.repository.FinanceRepositoryImpl
import com.example.shefarmai.ui.screens.finance.FinanceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: FakeFinanceRepository
    private lateinit var viewModel: FinanceViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = FakeFinanceRepository()
        viewModel = FinanceViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveRecord_withValidExpense_returnsTrue() {
        val success = viewModel.saveRecord(
            id = 0,
            type = TransactionType.EXPENSE,
            category = "Fertilizer",
            amount = 5000.0,
            description = "DAP Purchase"
        )

        assertTrue(success)
    }

    @Test
    fun saveRecord_withZeroOrNullAmount_returnsFalse() {
        val successZero = viewModel.saveRecord(
            id = 0,
            type = TransactionType.EXPENSE,
            category = "Fertilizer",
            amount = 0.0,
            description = "DAP Purchase"
        )
        assertFalse(successZero)

        val successNull = viewModel.saveRecord(
            id = 0,
            type = TransactionType.EXPENSE,
            category = "Fertilizer",
            amount = null,
            description = "DAP Purchase"
        )
        assertFalse(successNull)
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

        override suspend fun deleteRecord(record: FinancialRecord) {
            val newList = records.value.toMutableList()
            newList.removeIf { it.id == record.id }
            records.value = newList
        }

        override suspend fun deleteRecordById(id: Long) {}

        override suspend fun seedSampleDataIfEmpty() {}
    }
}
