package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.FinancialSummary
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getAllRecords(): Flow<List<FinancialRecord>>
    fun getSummary(): Flow<FinancialSummary>
    suspend fun getRecordById(id: Long): FinancialRecord?
    suspend fun addRecord(record: FinancialRecord): Long
    suspend fun updateRecord(record: FinancialRecord)
    suspend fun deleteRecord(record: FinancialRecord)
    suspend fun deleteRecordById(id: Long)
    suspend fun seedSampleDataIfEmpty()
}
