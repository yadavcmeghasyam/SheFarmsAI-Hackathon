package com.example.shefarmai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.TransactionType

@Entity(tableName = "financial_records")
data class FinancialRecordEntity(
    @get:PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "REVENUE" or "EXPENSE"
    val category: String,
    val amount: Double,
    val description: String,
    val dateInMillis: Long
) {
    fun toDomainModel(): FinancialRecord {
        return FinancialRecord(
            id = id,
            type = if (type == TransactionType.REVENUE.name) TransactionType.REVENUE else TransactionType.EXPENSE,
            category = category,
            amount = amount,
            description = description,
            dateInMillis = dateInMillis
        )
    }

    companion object {
        fun fromDomainModel(record: FinancialRecord): FinancialRecordEntity {
            return FinancialRecordEntity(
                id = record.id,
                type = record.type.name,
                category = record.category,
                amount = record.amount,
                description = record.description,
                dateInMillis = record.dateInMillis
            )
        }
    }
}
