package com.example.shefarmai.data.model

data class FinancialRecord(
    val id: Long = 0,
    val type: TransactionType,
    val category: String,
    val amount: Double,
    val description: String,
    val dateInMillis: Long = System.currentTimeMillis()
)
