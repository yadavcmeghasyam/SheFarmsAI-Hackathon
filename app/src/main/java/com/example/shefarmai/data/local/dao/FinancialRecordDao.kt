package com.example.shefarmai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shefarmai.data.local.entity.FinancialRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialRecordDao {
    @Query("SELECT * FROM financial_records ORDER BY dateInMillis DESC")
    fun getAllRecords(): Flow<List<FinancialRecordEntity>>

    @Query("SELECT * FROM financial_records WHERE type = :type ORDER BY dateInMillis DESC")
    fun getRecordsByType(type: String): Flow<List<FinancialRecordEntity>>

    @Query("SELECT * FROM financial_records WHERE id = :id")
    suspend fun getRecordById(id: Long): FinancialRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FinancialRecordEntity): Long

    @Update
    suspend fun updateRecord(record: FinancialRecordEntity)

    @Delete
    suspend fun deleteRecord(record: FinancialRecordEntity)

    @Query("DELETE FROM financial_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("SELECT COUNT(*) FROM financial_records")
    suspend fun getCount(): Int
}
