package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.AssistantMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AssistantRepositoryImpl : AssistantRepository {

    private val initialWelcomeMessage = AssistantMessage(
        id = "msg_welcome",
        isUser = false,
        text = "Jambo! I am your SheFarm Agricultural Assistant (On-Device Knowledge Engine). I can help you with crop pest management, soil health, financial profit calculations, market selling tips, and women farmer grant programs. How can I assist your farm today?",
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

    override suspend fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = AssistantMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = userText.trim()
        )

        val currentList = _messages.value.toMutableList()
        currentList.add(userMessage)
        _messages.value = currentList

        val responseText = generateAssistantResponse(userText)
        val assistantMessage = AssistantMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            text = responseText,
            quickActions = getRelevantQuickActions()
        )

        val updatedList = _messages.value.toMutableList()
        updatedList.add(assistantMessage)
        _messages.value = updatedList
    }

    override suspend fun clearHistory() {
        _messages.value = listOf(initialWelcomeMessage)
    }

    private fun generateAssistantResponse(query: String): String {
        val lower = query.lowercase()

        return when {
            lower.contains("armyworm") || lower.contains("pest") || lower.contains("caterpillar") || lower.contains("worm") -> {
                """
                🐛 Fall Armyworm & Pest Control Guidance:
                
                1. Early Symptoms: Look for 'window-pane' leaf damage and sawdust-like frass inside maize whorls.
                2. Natural Treatment:
                   - Mix 50g neem seed cake powder or wood ash in 1 Liter water with a drop of liquid soap.
                   - Apply into the whorls early in the morning or evening.
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
                   - Place 2 handfuls of well-rotted compost per planting hole.
                   - Top-dress with CAN or organic tea 3 weeks after germination.
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

            lower.contains("dry") || lower.contains("drought") || lower.contains("water") || lower.contains("irrigation") || lower.contains("weather") -> {
                """
                💧 Water Conservation & Drought Resilience:
                
                1. Low-Cost Drip Kits: Direct water to root zones using raised gravity barrels, reducing water consumption by 60%.
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
