package com.example.shefarmai

import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.TransactionType
import com.example.shefarmai.data.repository.FinanceRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FinancialCalculationsTest {

    @Test
    fun calculateSummary_withProfitableTransactions_returnsCorrectNetProfitAndMargin() {
        val records = listOf(
            FinancialRecord(
                id = 1,
                type = TransactionType.REVENUE,
                category = "Maize Sales",
                amount = 50000.0,
                description = "20 bags sold"
            ),
            FinancialRecord(
                id = 2,
                type = TransactionType.EXPENSE,
                category = "Fertilizer",
                amount = 10000.0,
                description = "DAP"
            ),
            FinancialRecord(
                id = 3,
                type = TransactionType.EXPENSE,
                category = "Seeds",
                amount = 5000.0,
                description = "Hybrid seeds"
            )
        )

        val summary = FinanceRepositoryImpl.calculateSummary(records)

        assertEquals(50000.0, summary.totalRevenue, 0.01)
        assertEquals(15000.0, summary.totalExpenses, 0.01)
        assertEquals(35000.0, summary.netProfitLoss, 0.01)
        assertEquals(70.0, summary.profitMarginPercentage, 0.01)
        assertTrue(summary.isProfitable)
        assertEquals(3, summary.transactionCount)
    }

    @Test
    fun calculateSummary_withLossTransactions_returnsNegativeProfitAndIsNotProfitable() {
        val records = listOf(
            FinancialRecord(
                id = 1,
                type = TransactionType.REVENUE,
                category = "Vegetable Sales",
                amount = 8000.0,
                description = "Tomatoes"
            ),
            FinancialRecord(
                id = 2,
                type = TransactionType.EXPENSE,
                category = "Irrigation Repair",
                amount = 12000.0,
                description = "Pump repair"
            )
        )

        val summary = FinanceRepositoryImpl.calculateSummary(records)

        assertEquals(8000.0, summary.totalRevenue, 0.01)
        assertEquals(12000.0, summary.totalExpenses, 0.01)
        assertEquals(-4000.0, summary.netProfitLoss, 0.01)
        assertEquals(-50.0, summary.profitMarginPercentage, 0.01)
        assertFalse(summary.isProfitable)
    }

    @Test
    fun calculateSummary_emptyRecords_returnsZeroes() {
        val summary = FinanceRepositoryImpl.calculateSummary(emptyList())

        assertEquals(0.0, summary.totalRevenue, 0.01)
        assertEquals(0.0, summary.totalExpenses, 0.01)
        assertEquals(0.0, summary.netProfitLoss, 0.01)
        assertEquals(0.0, summary.profitMarginPercentage, 0.01)
        assertTrue(summary.isProfitable)
        assertEquals(0, summary.transactionCount)
    }

    @Test
    fun calculateSummary_correctlyAggregatesCategories() {
        val records = listOf(
            FinancialRecord(id = 1, type = TransactionType.REVENUE, category = "Maize Sales", amount = 20000.0, description = ""),
            FinancialRecord(id = 2, type = TransactionType.REVENUE, category = "Maize Sales", amount = 30000.0, description = ""),
            FinancialRecord(id = 3, type = TransactionType.EXPENSE, category = "Fertilizer", amount = 8000.0, description = ""),
            FinancialRecord(id = 4, type = TransactionType.EXPENSE, category = "Fertilizer", amount = 4000.0, description = "")
        )

        val summary = FinanceRepositoryImpl.calculateSummary(records)

        assertEquals(50000.0, summary.revenueByCategory["Maize Sales"] ?: 0.0, 0.01)
        assertEquals(12000.0, summary.expenseByCategory["Fertilizer"] ?: 0.0, 0.01)
    }
}
