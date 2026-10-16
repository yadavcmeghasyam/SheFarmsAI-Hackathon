package com.example.shefarmai

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shefarmai.data.local.LocalChatHistoryStore
import com.example.shefarmai.data.local.RoomChatHistoryStore
import com.example.shefarmai.data.local.SheFarmDatabase
import com.example.shefarmai.data.model.FinancialRecord
import com.example.shefarmai.data.model.ProductListing
import com.example.shefarmai.data.model.ProductStatus
import com.example.shefarmai.data.model.TransactionType
import com.example.shefarmai.data.repository.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PersistenceTest {
    @Test fun roomDataSurvivesReopenAndDeletionsStayDeleted() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "test-${UUID.randomUUID()}.db"
        fun open() = Room.databaseBuilder(context, SheFarmDatabase::class.java, name).build()
        var db = open()
        try {
            val market = MarketplaceRepositoryImpl(db.productListingDao())
            val finance = FinanceRepositoryImpl(db.financialRecordDao())
            val listingId = market.addListing(ProductListing(title = "Test maize", category = "Grains",
                pricePerUnit = 100.0, unit = "kg", availableQuantity = 5.0, location = "Farm",
                contactInfo = "0700", description = "", datePostedInMillis = 1000L, status = ProductStatus.SOLD))
            val recordId = finance.addRecord(FinancialRecord(type = TransactionType.REVENUE,
                category = "Sales", amount = 500.0, description = "", dateInMillis = 2000L))
            db.close()
            db = open()
            val reopenedMarket = MarketplaceRepositoryImpl(db.productListingDao())
            val reopenedFinance = FinanceRepositoryImpl(db.financialRecordDao())
            assertEquals(ProductStatus.SOLD, reopenedMarket.getListingById(listingId)?.status)
            assertEquals(2000L, reopenedFinance.getRecordById(recordId)?.dateInMillis)
            assertEquals(500.0, reopenedFinance.getSummary().first().netProfitLoss, 0.01)
            reopenedMarket.deleteListingById(listingId)
            reopenedFinance.deleteRecordById(recordId)
            db.close()
            db = open()
            assertTrue(db.productListingDao().getAllListings().first().isEmpty())
            assertTrue(db.financialRecordDao().getAllRecords().first().isEmpty())
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }

    @Test fun actualChatStorageRoundTripsAndClearRemovesHistory() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "test-chat-${UUID.randomUUID()}"
        try {
            val first = AssistantRepositoryImpl(LocalChatHistoryStore(context, name))
            first.sendMessage("Organic compost recipe")
            val second = AssistantRepositoryImpl(LocalChatHistoryStore(context, name))
            second.loadHistory()
            assertEquals(first.messages.value, second.messages.value)
            second.clearHistory()
            val third = AssistantRepositoryImpl(LocalChatHistoryStore(context, name))
            third.loadHistory()
            assertEquals(1, third.messages.value.size)
        } finally {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit()
        }
    }

    @Test fun roomChatHistorySurvivesReopenAndClearsProperly() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "test-chat-room-${UUID.randomUUID()}.db"
        fun open() = Room.databaseBuilder(context, SheFarmDatabase::class.java, name).build()
        var db = open()
        try {
            val store = RoomChatHistoryStore(db.chatMessageDao())
            val first = AssistantRepositoryImpl(store)
            first.sendMessage("Organic compost recipe")
            db.close()
            db = open()
            val reopenedStore = RoomChatHistoryStore(db.chatMessageDao())
            val second = AssistantRepositoryImpl(reopenedStore)
            second.loadHistory()
            assertEquals(first.messages.value, second.messages.value)
            second.clearHistory()
            db.close()
            db = open()
            val thirdStore = RoomChatHistoryStore(db.chatMessageDao())
            val third = AssistantRepositoryImpl(thirdStore)
            third.loadHistory()
            assertEquals(1, third.messages.value.size)
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }
}
