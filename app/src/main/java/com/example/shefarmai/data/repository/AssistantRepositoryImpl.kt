package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.AssistantMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AssistantRepositoryImpl(private val historyStore: ChatHistoryStore? = null) : AssistantRepository {

    private val mutex = Mutex()
    private var loaded = false

    private val initialWelcomeMessage = AssistantMessage(
        id = "msg_welcome",
        isUser = false,
        text = "Jambo! I am your offline farming helper. I use a built-in topic library, not a generative AI model. Ask about pest scouting, compost, water, farm accounting, selling produce or farmer groups. I cannot diagnose crop disease, check live weather or verify current grants.",
        quickActions = listOf(
            "How to treat fall armyworm?",
            "How to calculate crop profit margin?",
            "Organic compost recipe",
            "Best crops for dry season",
            "Grants for women farm groups"
        )
    )

    private val _messages = MutableStateFlow(listOf(initialWelcomeMessage))
    override val messages: StateFlow<List<AssistantMessage>> = _messages.asStateFlow()

    override suspend fun loadHistory() = mutex.withLock { loadIfNeeded() }

    private suspend fun loadIfNeeded() {
        if (!loaded) {
            val saved = historyStore?.load().orEmpty()
            if (saved.isNotEmpty()) _messages.value = saved
            loaded = true
        }
    }

    override suspend fun sendMessage(userText: String) = mutex.withLock {
        if (userText.isBlank()) return@withLock
        loadIfNeeded()

        val userMessage = AssistantMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = userText.trim()
        )

        val responseText = generateAssistantResponse(userText)
        val assistantMessage = AssistantMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            text = responseText,
            quickActions = getRelevantQuickActions()
        )

        val updatedList = _messages.value.toMutableList()
        updatedList.add(userMessage)
        updatedList.add(assistantMessage)
        // Persist a complete exchange before publishing it to the screen.
        historyStore?.save(updatedList)
        _messages.value = updatedList
    }

    override suspend fun clearHistory() = mutex.withLock {
        historyStore?.save(emptyList())
        _messages.value = listOf(initialWelcomeMessage)
        loaded = true
    }

    private fun generateAssistantResponse(query: String): String {
        val lower = query.lowercase()

        return when {
            lower.contains("weather") || lower.contains("forecast") || lower.contains("today's price") -> {
                "I cannot access live weather or market prices. Check a local forecast or buyer quote. For planning, ask about water conservation, crop storage or produce selling tips."
            }
            lower.contains("market") || lower.contains("sell") || lower.contains("buyer") || lower.contains("price") -> {
                "Produce selling checklist:\n1. Record crop quality, available quantity, unit, asking price and pickup location.\n2. Compare quotes from several buyers and include transport costs.\n3. Agree on grading, weighing and payment terms before delivery.\n4. Use the Marketplace tab to keep local listings and mark them Reserved or Sold. Listings are not published online and no buyer matching or payments are provided."
            }
            lower.contains("armyworm") || lower.contains("pest") || lower.contains("caterpillar") || lower.contains("worm") -> {
                """
                🐛 Fall Armyworm & Pest Control Guidance:
                
                1. Early Symptoms: Look for 'window-pane' leaf damage and sawdust-like frass inside maize whorls.
                2. Natural Treatment:
                   - Confirm the pest with a local agricultural extension officer before treatment.
                   - Use locally approved crop protection products only as directed by their labels.
                3. Biological Defense: Intercrop maize with Desmodium legumes (Push-Pull method) to repel egg-laying moths.
                
                💡 Tip: Check your fields twice weekly during early crop emergence!
                """.trimIndent()
            }

            lower.contains("profit") || lower.contains("loss") || lower.contains("calculate") || lower.contains("margin") || lower.contains("finance") || lower.contains("accounting") -> {
                """
                📊 Farm Financial & Profitability Guidance:
                
                - Formula: Net Profit = Total Sales Revenue - Total Expenses
                - Profit Margin % = (Net Profit / Total Sales) × 100
                
                Example Calculation:
                If you harvest and sell 20 bags of maize for KSh 56,000, and your total input costs (seeds, fertilizer, labor) were KSh 31,200:
                • Net Profit = KSh 56,000 - KSh 31,200 = KSh 24,800
                • Profit Margin = (24,800 / 56,000) × 100 = 44.28%
                
                💡 Tip: Use our 'Finance Tracker' tab to log daily expenses and track seasonal profitability!
                """.trimIndent()
            }

            lower.contains("compost") || lower.contains("fertilizer") || lower.contains("soil") || lower.contains("dap") || lower.contains("npk") || lower.contains("manure") -> {
                """
                🌱 Soil Fertility & Composting Guide:
                
                1. Compost Layering Ratio (3 Browns : 1 Green):
                   - Browns: Dry maize stalks, straw, dry leaves (Carbon).
                   - Greens: Fresh cow dung, legume leaves, green weeds (Nitrogen).
                2. Application:
                   - Use fully matured compost and choose application rates for your soil and crop.
                   - Obtain a soil test and local extension advice before adding fertilizer.
                3. Soil Conservation: Keep soil covered with straw mulch to retain moisture and suppress weeds.
                """.trimIndent()
            }

            lower.contains("grant") || lower.contains("group") || lower.contains("chama") || lower.contains("cooperative") || lower.contains("funding") || lower.contains("loan") -> {
                """
                👩‍🌾 Women Farmer Groups & Grant Opportunities:
                
                1. Chama Table Banking: Establish weekly group savings to fund seasonal inputs without high interest.
                2. Cooperative Registration: Register your women's farming group with local Social Services to qualify for agricultural development micro-grants.
                3. Collective Bargaining: Pool crop harvests together with group members to command higher wholesale prices from aggregators.
                
                💡 Tip: Check our 'Farming Guides' tab for detailed steps on setting up audited farm cooperatives!
                """.trimIndent()
            }

            lower.contains("dry") || lower.contains("drought") || lower.contains("water") || lower.contains("irrigation") -> {
                """
                💧 Water Conservation & Drought Resilience:
                
                1. Low-Cost Drip Kits: Direct water to root zones using raised gravity barrels, reducing water losses when correctly designed and maintained.
                2. Drought-Tolerant Crops: Consider sweet cassava, sorghum, cowpeas, and pearl millet in low-rainfall seasons.
                3. Mulching: Cover soil surfaces with 5cm dry crop residue to cut soil evaporation.
                """.trimIndent()
            }

            lower.contains("tomato") || lower.contains("blight") || lower.contains("vegetable") -> {
                """
                🍅 Vegetable & Tomato Care Guide:
                
                1. Early Blight Defense: Space plants 45cm apart for adequate airflow and stake vines above wet ground.
                2. Water Technique: Always water at the base of tomato plants; avoid wetting leaves to stop fungal spores.
                3. Harvesting: Pick tomatoes when firm pink for long-distance transport to local markets.
                """.trimIndent()
            }

            else -> {
                """
                🌾 SheFarm Agricultural Support:
                
                Thank you for your question! As your on-device farming assistant, I am optimized to assist with:
                • Crop disease & pest solutions (Fall Armyworm, Blight, Weevils)
                • Soil health & organic composting
                • Seasonal financial & profit margin calculations
                • Marketplace listing strategies for women farmers
                • Chama savings & group funding guides
                
                Try clicking one of the quick suggested questions below or ask about a specific crop!
                """.trimIndent()
            }
        }
    }

    private fun getRelevantQuickActions(): List<String> {
        return listOf(
            "How to treat fall armyworm?",
            "Organic compost recipe",
            "Calculate profit margin",
            "Grants for women farm groups"
        )
    }
}
