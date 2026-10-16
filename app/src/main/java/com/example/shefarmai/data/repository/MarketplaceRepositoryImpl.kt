package com.example.shefarmai.data.repository

import com.example.shefarmai.data.local.dao.ProductListingDao
import com.example.shefarmai.data.local.entity.ProductListingEntity
import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.model.ProductStatus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MarketplaceRepositoryImpl(
    private val dao: ProductListingDao
) : MarketplaceRepository {

    private val seedMutex = Mutex()

    override fun getAllListings(): Flow<List<ProductListing>> {
        return dao.getAllListings().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getListingById(id: Long): ProductListing? {
        return dao.getListingById(id)?.toDomainModel()
    }

    override suspend fun addListing(listing: ProductListing): Long {
        return dao.insertListing(ProductListingEntity.fromDomainModel(listing))
    }

    override suspend fun updateListing(listing: ProductListing) {
        dao.updateListing(ProductListingEntity.fromDomainModel(listing))
    }

    override suspend fun deleteListing(listing: ProductListing) {
        dao.deleteListing(ProductListingEntity.fromDomainModel(listing))
    }

    override suspend fun deleteListingById(id: Long) {
        dao.deleteListingById(id)
    }

    override suspend fun seedSampleDataIfEmpty() = seedMutex.withLock {
        if (dao.getCount() == 0) {
            val samples = listOf(
                ProductListing(
                    title = "Organic White Maize",
                    category = "Grains",
                    pricePerUnit = 2800.0,
                    unit = "50kg bag",
                    availableQuantity = 25.0,
                    location = "Eldoret, Uasin Gishu",
                    contactInfo = "+254 712 345 678",
                    description = "High quality sun-dried organic white maize. Cleaned and ready for milling or wholesale purchase.",
                    status = ProductStatus.AVAILABLE
                ),
                ProductListing(
                    title = "Fresh Roma Tomatoes",
                    category = "Vegetables",
                    pricePerUnit = 90.0,
                    unit = "kg",
                    availableQuantity = 120.0,
                    location = "Naivasha, Nakuru",
                    contactInfo = "+254 723 456 789",
                    description = "Farm-fresh, sun-ripened firm Roma tomatoes ideal for fresh produce markets and catering.",
                    status = ProductStatus.AVAILABLE
                ),
                ProductListing(
                    title = "Grade A Cassava Tubers",
                    category = "Tubers",
                    pricePerUnit = 45.0,
                    unit = "kg",
                    availableQuantity = 350.0,
                    location = "Machakos",
                    contactInfo = "+254 734 567 890",
                    description = "Freshly harvested sweet cassava roots suitable for domestic consumption or starch processing.",
                    status = ProductStatus.AVAILABLE
                ),
                ProductListing(
                    title = "Dried Sunflower Seeds",
                    category = "Oilseeds",
                    pricePerUnit = 130.0,
                    unit = "kg",
                    availableQuantity = 75.0,
                    location = "Kitale, Trans-Nzoia",
                    contactInfo = "+254 745 678 901",
                    description = "High oil yield dried sunflower seeds cleaned and sorted for oil extraction or bird feed.",
                    status = ProductStatus.AVAILABLE
                ),
                ProductListing(
                    title = "Fresh Dairy Cow Milk",
                    category = "Dairy",
                    pricePerUnit = 75.0,
                    unit = "Liter",
                    availableQuantity = 60.0,
                    location = "Kiambu",
                    contactInfo = "+254 756 789 012",
                    description = "Pure fresh morning milk from healthy zero-grazed Friesian cows. Daily delivery available.",
                    status = ProductStatus.AVAILABLE
                )
            )

            samples.forEach { addListing(it.copy(description = "[DEMO] " + it.description)) }
        }
    }
}
