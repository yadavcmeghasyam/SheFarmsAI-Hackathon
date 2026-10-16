package com.example.shefarmai

import com.example.shefarmai.data.model.*
import com.example.shefarmai.data.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFinanceRepository : FinanceRepository {
    val records = MutableStateFlow<List<FinancialRecord>>(emptyList())
    var failWrites = false
    var seedCalls = 0
    override fun getAllRecords() = records
    override fun getSummary() = records.map { FinanceRepositoryImpl.calculateSummary(it) }
    override suspend fun getRecordById(id: Long) = records.value.find { it.id == id }
    override suspend fun addRecord(record: FinancialRecord): Long {
        check(!failWrites)
        val id = (records.value.maxOfOrNull { it.id } ?: 0) + 1
        records.value += record.copy(id = id)
        return id
    }
    override suspend fun updateRecord(record: FinancialRecord) {
        check(!failWrites)
        records.value = records.value.map { if (it.id == record.id) record else it }
    }
    override suspend fun deleteRecord(record: FinancialRecord) = deleteRecordById(record.id)
    override suspend fun deleteRecordById(id: Long) {
        check(!failWrites)
        records.value = records.value.filterNot { it.id == id }
    }
    override suspend fun seedSampleDataIfEmpty() { seedCalls++ }
}

class FakeMarketplaceRepository : MarketplaceRepository {
    val listings = MutableStateFlow<List<ProductListing>>(emptyList())
    var failWrites = false
    var seedCalls = 0
    override fun getAllListings() = listings
    override suspend fun getListingById(id: Long) = listings.value.find { it.id == id }
    override suspend fun addListing(listing: ProductListing): Long {
        check(!failWrites)
        val id = (listings.value.maxOfOrNull { it.id } ?: 0) + 1
        listings.value += listing.copy(id = id)
        return id
    }
    override suspend fun updateListing(listing: ProductListing) {
        check(!failWrites)
        listings.value = listings.value.map { if (it.id == listing.id) listing else it }
    }
    override suspend fun deleteListing(listing: ProductListing) = deleteListingById(listing.id)
    override suspend fun deleteListingById(id: Long) {
        check(!failWrites)
        listings.value = listings.value.filterNot { it.id == id }
    }
    override suspend fun seedSampleDataIfEmpty() { seedCalls++ }
}

fun sampleListing(id: Long = 1, status: ProductStatus = ProductStatus.AVAILABLE, date: Long = 1000L) = ProductListing(
    id, "Maize", "Grains", 100.0, "kg", 5.0, "Eldoret", "0700000000", "Harvest", date, status
)

class MemoryChatHistory : ChatHistoryStore {
    var saved = emptyList<AssistantMessage>()
    var failWrites = false
    override suspend fun load() = saved
    override suspend fun save(messages: List<AssistantMessage>) {
        check(!failWrites)
        saved = messages.toList()
    }
}
