package com.example.shefarmai.data.model

data class ProductListing(
    val id: Long = 0,
    val title: String,
    val category: String,
    val pricePerUnit: Double,
    val unit: String,
    val availableQuantity: Double,
    val location: String,
    val contactContactInfo: String,
    val description: String,
    val datePostedInMillis: Long = System.currentTimeMillis(),
    val status: String = "AVAILABLE" // AVAILABLE, RESERVED, SOLD
)
