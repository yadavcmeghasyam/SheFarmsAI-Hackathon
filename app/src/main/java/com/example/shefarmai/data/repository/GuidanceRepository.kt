package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.FarmingGuide
import kotlinx.coroutines.flow.Flow

interface GuidanceRepository {
    fun getGuides(): Flow<List<FarmingGuide>>
    fun getGuideById(id: String): FarmingGuide?
    fun searchGuides(query: String, category: String? = null): Flow<List<FarmingGuide>>
}
