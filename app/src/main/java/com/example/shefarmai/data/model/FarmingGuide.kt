package com.example.shefarmai.data.model

data class FarmingGuide(
    val id: String,
    val title: String,
    val category: String, // e.g. Crop Care, Pest Control, Soil Health, Business & Funding
    val summary: String,
    val detailContent: String,
    val actionableTips: List<String>,
    val suitableCrops: List<String>
)
