package com.example.shefarmai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shefarmai.data.model.ProductListing

@Entity(tableName = "product_listings")
data class ProductListingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val pricePerUnit: Double,
    val unit: String,
    val availableQuantity: Double,
    val location: String,
    val contactInfo: String,
    val description: String,
    val datePostedInMillis: Long,
    val status: String
) {
    fun toDomainModel(): ProductListing {
        return ProductListing(
            id = id,
            title = title,
            category = category,
            pricePerUnit = pricePerUnit,
            unit = unit,
            availableQuantity = availableQuantity,
            location = location,
            contactContactInfo = contactInfo,
            description = description,
            datePostedInMillis = datePostedInMillis,
            status = status
        )
    }

    companion object {
        fun fromDomainModel(listing: ProductListing): ProductListingEntity {
            return ProductListingEntity(
                id = listing.id,
                title = listing.title,
                category = listing.category,
                pricePerUnit = listing.pricePerUnit,
                unit = listing.unit,
                availableQuantity = listing.availableQuantity,
                location = listing.location,
                contactInfo = listing.contactContactInfo,
                description = listing.description,
                datePostedInMillis = listing.datePostedInMillis,
                status = listing.status
            )
        }
    }
}
