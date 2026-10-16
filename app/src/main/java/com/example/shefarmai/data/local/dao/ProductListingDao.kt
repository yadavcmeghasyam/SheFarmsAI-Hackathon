package com.example.shefarmai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shefarmai.data.local.entity.ProductListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductListingDao {
    @Query("SELECT * FROM product_listings ORDER BY datePostedInMillis DESC")
    fun getAllListings(): Flow<List<ProductListingEntity>>

    @Query("SELECT * FROM product_listings WHERE id = :id")
    suspend fun getListingById(id: Long): ProductListingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ProductListingEntity): Long

    @Update
    suspend fun updateListing(listing: ProductListingEntity)

    @Delete
    suspend fun deleteListing(listing: ProductListingEntity)

    @Query("DELETE FROM product_listings WHERE id = :id")
    suspend fun deleteListingById(id: Long)

    @Query("SELECT COUNT(*) FROM product_listings")
    suspend fun getCount(): Int
}
