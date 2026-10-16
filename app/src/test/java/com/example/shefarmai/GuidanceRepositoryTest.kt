package com.example.shefarmai

import com.example.shefarmai.data.repository.GuidanceRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class GuidanceRepositoryTest {
    @Test fun searchesCropAndTextWithCategoryTogether() = runTest {
        val repo = GuidanceRepositoryImpl()
        val compost = repo.searchGuides(" compost ", "Soil Health").first()
        assertEquals(1, compost.size)
        assertTrue(compost.single().title.contains("Compost"))
        assertTrue(repo.searchGuides("compost", "Pest Control").first().isEmpty())
        assertTrue(repo.searchGuides("Maize", "All").first().isNotEmpty())
        assertEquals(6, repo.searchGuides("", "All").first().size)
    }
}
