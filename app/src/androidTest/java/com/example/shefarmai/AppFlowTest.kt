package com.example.shefarmai

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.shefarmai.data.local.SheFarmDatabase
import com.example.shefarmai.data.repository.AssistantRepositoryImpl
import com.example.shefarmai.ui.navigation.AppNavigation
import com.example.shefarmai.ui.theme.SheFarmAITheme
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppFlowTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var database: SheFarmDatabase

    @Before fun setUp() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), SheFarmDatabase::class.java).build()
        compose.setContent {
            SheFarmAITheme {
                AppNavigation(database, AssistantRepositoryImpl())
            }
        }
    }

    @After fun tearDown() { database.close() }

    @Test fun navigationFinanceCrudGuidesAndAssistantWork() {
        compose.onNodeWithText("Load sample farm (demo records)").assertExists()
        compose.onNodeWithContentDescription("Finances").performClick()
        compose.onNodeWithContentDescription("Add Financial Record").performClick()
        compose.onNodeWithText("Category *").performTextInput("Test seeds")
        compose.onNodeWithText("Amount (KSh) *").performTextInput("250")
        compose.onNodeWithText("Save Record").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Test seeds").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithContentDescription("Edit Transaction").performClick()
        compose.onNodeWithText("Amount (KSh) *").performTextReplacement("300")
        compose.onNodeWithText("Update").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Update").fetchSemanticsNodes().isEmpty() }
        compose.onNodeWithContentDescription("Delete Transaction").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Test seeds").fetchSemanticsNodes().isEmpty() }
        compose.onNodeWithContentDescription("Guides").performClick()
        compose.onNodeWithText("Fall Armyworm Identification & Control").performClick()
        compose.onNodeWithText("Close").performClick()
        compose.onNodeWithContentDescription("AI Assistant").performClick()
        compose.onNodeWithText("Ask about crops, pests, compost, finances...").performTextInput("How do I sell produce?")
        compose.onNodeWithContentDescription("Send Message").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Produce selling checklist:", substring = true).fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithContentDescription("Clear Chat").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Produce selling checklist:", substring = true).fetchSemanticsNodes().isEmpty() }
    }

    @Test fun marketplaceCreateEditStatusSearchAndDelete() {
        compose.onNodeWithContentDescription("Marketplace").performClick()
        compose.onNodeWithContentDescription("Add Listing").performClick()
        compose.onNodeWithText("Crop / Produce Title *").performScrollTo().performTextInput("Test harvest")
        compose.onNodeWithText("Price per Unit (KSh) *").performScrollTo().performTextInput("100")
        compose.onNodeWithText("Quantity *").performScrollTo().performTextInput("5")
        compose.onNodeWithText("Location / County *").performScrollTo().performTextInput("Test farm")
        compose.onNodeWithText("Contact Info / Phone *").performScrollTo().performTextInput("0700000000")
        compose.onNodeWithText("Post Listing").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Test harvest").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithContentDescription("Edit Listing").performClick()
        compose.onNodeWithText("Sold").performScrollTo().performClick()
        compose.onNodeWithText("Update").performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Status: Sold").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Search crops, location, or description...").performTextInput("missing")
        compose.waitUntil(10000) { compose.onAllNodesWithText("Test harvest").fetchSemanticsNodes().isEmpty() }
        compose.onNode(hasSetTextAction()).performTextClearance()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Test harvest").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithContentDescription("Delete Listing").performScrollTo().performClick()
        compose.waitUntil(10000) { compose.onAllNodesWithText("Test harvest").fetchSemanticsNodes().isEmpty() }
    }
}
