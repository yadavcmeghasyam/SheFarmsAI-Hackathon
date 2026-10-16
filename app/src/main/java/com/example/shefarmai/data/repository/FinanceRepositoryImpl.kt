package com.example.shefarmai.data.repository

import com.example.shefarmai.data.local.dao.FinancialRecordDao
import com.example.shefarmai.data.local.entity.FinancialRecordEntity
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.FinancialSummary
import com.example.shefarmai.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinanceRepositoryImpl(
    private val dao: FinancialRecordDao
) : FinanceRepository {

    override fun getAllRecords(): Flow<List<FinancialRecord>> {
        return dao.getAllRecords().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSummary(): Flow<FinancialSummary> {
        return getAllRecords().map { records ->
            calculateSummary(records)
        }
    }

    override suspend fun getRecordById(id: Long): FinancialRecord? {
        return dao.getRecordById(id)?.toDomainModel()
    }

    override suspend fun addRecord(record: FinancialRecord): Long {
        return dao.insertRecord(FinancialRecordEntity.fromDomainModel(record))
    }

    override suspend fun updateRecord(record: FinancialRecord) {
        dao.updateRecord(FinancialRecordEntity.fromDomainModel(record))
    }

    override suspend fun deleteRecord(record: FinancialRecord) {
        dao.deleteRecord(FinancialRecordEntity.fromDomainModel(record))
    }

    override suspend fun deleteRecordById(id: Long) {
        dao.deleteRecordById(id)
    }

    override suspend fun seedSampleDataIfEmpty() {
        if (dao.getCount() == 0) {
            val now = System.currentTimeMillis()
            val dayInMillis = 86_400_000L

            val samples = listOf(
                FinancialRecord(
                    type = TransactionType.REVENUE,
                    category = "Maize Harvest Sales",
                    amount = 56000.0,
                    description = "Sold 20 bags of white maize to local cooperative buyer",
                    dateInMillis = now - (2 * dayInMillis)
                ),
                FinancialRecord(
                    type = TransactionType.EXPENSE,
                    category = "Fertilizer & Soil",
                    amount = 14500.0,
                    description = "Purchased 2 bags DAP fertilizer for seasonal planting",
                    dateInMillis = now - (15 * dayInMillis)
                ),
                FinancialRecord(
                    type = TransactionType.EXPENSE,
                    category = "Seeds & Planting",
                    amount = 7200.0,
                    description = "Certified hybrid maize and tomato seed packages",
                    dateInMillis = now - (20 * dayInMillis)
                ),
                FinancialRecord(
                    type = TransactionType.REVENUE,
                    category = "Vegetable Market",
                    amount = 18400.0,
                    description = "Weekend sales of fresh Roma tomatoes and kale",
                    dateInMillis = now - (5 * dayInMillis)
                ),
                FinancialRecord(
                    type = TransactionType.EXPENSE,
                    category = "Farm Labor",
                    amount = 9500.0,
                    description = "Labor payments for weeding and harvesting team",
                    dateInMillis = now - (8 * dayInMillis)
                ),
                FinancialRecord(
                    type = TransactionType.EXPENSE,
                    category = "Equipment & Repairs",
                    amount = 4200.0,
                    description = "Repaired drip irrigation hoses and water pump filter",
                    dateInMillis = now - (12 * dayInMillis)
                ),
                FinancialRecord(
                    type = TransactionType.REVENUE,
                    category = "Dairy Sales",
                    amount = 12600.0,
                    description = "Bi-weekly fresh milk sales to dairy coop",
                    dateInMillis = now - (1 * dayInMillis)
                )
            )

            samples.forEach { addRecord(it) }
        }
    }

    companion object {
        fun calculateSummary(records: List<FinancialRecord>): FinancialSummary {
            var totalRev = 0.0
            var totalExp = 0.0
            val revCategoryMap = mutableMapOf<String, Double>()
            val expCategoryMap = mutableMapOf<String, Double>()

            for (record in records) {
                if (record.type == TransactionType.REVENUE) {
                    totalRev += record.amount
                    val currentCat = revCategoryMap.getOrDefault(record.category, 0.0)
                    revCategoryMap[record.category] = currentCat + record.amount
                } else {
                    totalExp += record.amount
                    val currentCat = expCategoryMap.getOrDefault(record.category, 0.0)
                    expCategoryMap[record.category] = currentCat + record.amount
                }
            }

            val netProfit = totalRev - totalExp
            val marginPercent = if (totalRev > 0.0) {
                (netProfit / totalRev) * 100.0
            } else {
                0.0
            }

            return FinancialSummary(
                totalRevenue = totalRev,
                totalExpenses = totalExp,
                netProfitLoss = netProfit,
                profitMarginPercentage = marginPercent,
                revenueByCategory = revCategoryMap,
                expenseByCategory = expCategoryMap,
                transactionCount = records.size
            )
        }
    }
}
