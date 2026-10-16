package com.example.shefarmai

import com.example.shefarmai.data.model.*
import com.example.shefarmai.ui.screens.finance.FinanceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModelTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun createEditFilterDeleteAndPreserveDate() = runTest {
        val repo = FakeFinanceRepository()
        val vm = FinanceViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        assertTrue(vm.saveRecord(type = TransactionType.REVENUE, category = " Sales ", amount = 500.0, description = "Maize"))
        runCurrent()
        val original = repo.records.value.single()
        assertEquals("Sales", original.category)
        assertEquals(500.0, vm.uiState.value.summary.netProfitLoss, 0.001)
        vm.saveRecord(original.id, TransactionType.REVENUE, "Sales", 600.0, "Edited")
        runCurrent()
        assertEquals(original.dateInMillis, repo.records.value.single().dateInMillis)
        assertEquals(600.0, vm.uiState.value.summary.netProfitLoss, 0.001)
        vm.onFilterSelected("EXPENSE")
        runCurrent()
        assertTrue(vm.uiState.value.records.isEmpty())
        vm.deleteRecord(original)
        runCurrent()
        assertEquals(0, vm.uiState.value.summary.transactionCount)
        assertEquals(0, repo.seedCalls)
    }

    @Test fun rejectsNonFiniteAndNonPositiveAmounts() = runTest {
        val repo = FakeFinanceRepository()
        val vm = FinanceViewModel(repo)
        for (amount in listOf(null, 0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY)) {
            assertFalse(vm.saveRecord(type = TransactionType.EXPENSE, category = "Seeds", amount = amount, description = ""))
        }
        runCurrent()
        assertTrue(repo.records.value.isEmpty())
    }

    @Test fun failedWriteKeepsDialogOpenAndAllowsRetry() = runTest {
        val repo = FakeFinanceRepository().apply { failWrites = true }
        val vm = FinanceViewModel(repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        var closed = false
        vm.saveRecord(type = TransactionType.EXPENSE, category = "Seeds", amount = 50.0, description = "", onSaved = { closed = true })
        assertFalse(vm.saveRecord(type = TransactionType.EXPENSE, category = "Seeds", amount = 50.0, description = ""))
        runCurrent()
        assertFalse(closed)
        assertFalse(vm.uiState.value.isSaving)
        assertNotNull(vm.uiState.value.errorMessage)
        repo.failWrites = false
        vm.saveRecord(type = TransactionType.EXPENSE, category = "Seeds", amount = 50.0, description = "", onSaved = { closed = true })
        runCurrent()
        assertTrue(closed)
        assertNull(vm.uiState.value.errorMessage)
    }
}
