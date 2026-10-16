package com.example.shefarmai.data.model

data class FinancialSummary(
    val totalRevenue: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netProfitLoss: Double = 0.0,
    val profitMarginPercentage: Double = 0.0,
    val revenueByCategory: Map<String, Double> = emptyMap(),
    val expenseByCategory: Map<String, Double> = emptyMap(),
    val transactionCount: Int = 0
) {
    val isProfitable: Boolean get() = netProfitLoss >= 0.0
}
