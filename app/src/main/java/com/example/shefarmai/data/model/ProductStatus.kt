package com.example.shefarmai.data.model

enum class ProductStatus(val displayName: String) {
    AVAILABLE("Available"),
    RESERVED("Reserved"),
    SOLD("Sold");

    companion object {
        fun fromString(value: String): ProductStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: AVAILABLE
        }
    }
}
