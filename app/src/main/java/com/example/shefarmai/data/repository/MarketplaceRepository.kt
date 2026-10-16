package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.ProductListing
import kotlinx.coroutines.flow.Flow

interface MarketplaceRepository {
    fun getAllListings(): Flow<List<ProductListing>>
    suspend fun getListingById(id: Long): ProductListing?
    suspend fun addListing(listing: ProductListing): Long
    suspend fun updateListing(listing: ProductListing)
    suspend fun deleteListing(listing: ProductListing)
    suspend fun deleteListingById(id: Long)
    suspend fun seedSampleDataIfEmpty()
}
