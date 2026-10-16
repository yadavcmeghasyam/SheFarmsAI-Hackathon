package com.example.shefarmai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shefarmai.data.local.dao.FinancialRecordDao
import com.example.shefarmai.data.local.dao.ProductListingDao
import com.example.shefarmai.data.local.entity.FinancialRecordEntity
import com.example.shefarmai.data.local.entity.ProductListingEntity

@Database(
    entities = [
        ProductListingEntity::class,
        FinancialRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SheFarmDatabase : RoomDatabase() {

    abstract fun productListingDao(): ProductListingDao
    abstract fun financialRecordDao(): FinancialRecordDao

    companion object {
        @Volatile
        private var INSTANCE: SheFarmDatabase? = null

        fun getDatabase(context: Context): SheFarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SheFarmDatabase::class.java,
                    "shefarm_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
