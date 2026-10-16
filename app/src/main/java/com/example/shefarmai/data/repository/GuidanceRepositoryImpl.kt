package com.example.shefarmai.data.repository

import com.example.shefarmai.data.model.FarmingGuide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class GuidanceRepositoryImpl : GuidanceRepository {

    private val sampleGuides = listOf(
        FarmingGuide(
            id = "guide_fall_armyworm",
            title = "Fall Armyworm Identification & Control",
            category = "Pest Control",
            summary = "Early detection and sustainable biological control strategies for fall armyworm in maize and sorghum.",
            detailContent = """
                Fall armyworm (Spodoptera frugiperda) is a destructive pest attacking grain crops. Early detection in the whorl stage is critical to preventing yield loss.
                
                Key Identification Signs:
                1. Window-pane damage on young leaves (scraped leaf surface).
                2. Sawdust-like frass (excrement) inside leaf whorls.
                3. Y-shaped mark on the front of the caterpillar's head.
                
                Integrated Management Protocol:
                - Frequent Scouting: Inspect 20 consecutive plants in 5 different spots twice weekly.
                - Treatment Selection: Confirm identification with a local agricultural extension officer; use locally approved products and follow their labels.
                - Pheromone Traps: Install 4 traps per hectare to monitor adult moth populations.
                - Push-Pull System: Intercrop maize with Desmodium to repel moths, and plant Napier grass along field borders to trap pests.
            """.trimIndent(),
            actionableTips = listOf(
                "Scout fields twice a week starting 7 days after germination.",
                "Crush egg masses manually when found during early growth stages.",
                "Choose treatment with your local extension officer and follow product labels."
            ),
            suitableCrops = listOf("Maize", "Sorghum", "Sweetcorn")
        ),
        FarmingGuide(
            id = "guide_composting",
            title = "Building Nutrient-Rich On-Farm Compost",
            category = "Soil Health",
            summary = "Transform crop residues, green manure, and animal dung into high-fertility humus to reduce fertilizer costs.",
            detailContent = """
                Composting turns raw organic matter into stabilized nutrients that improve soil moisture retention and microbial health.
                
                The Ideal Carbon-to-Nitrogen (C:N) Ratio:
                - Browns (Carbon source): Dried stalks, dry leaves, sawdust, rice husks (3 parts).
                - Greens (Nitrogen source): Fresh weed cuttings, legume biomass, fresh manure, kitchen scraps (1 part).
                
                Step-by-Step Pile Method:
                1. Base Layer: Place 15cm of coarse twigs/stalks for drainage.
                2. Alternating Layers: Add 15cm brown layer, followed by 5cm green layer, and a sprinkle of farmyard manure or topsoil.
                3. Moisture Check: Squeeze a handful of compost; it should feel like a damp wrung-out sponge without dripping water.
                4. Aeration: Turn the pile every 14 days using a pitchfork to supply oxygen. The pile temperature should rise to 55-65°C.
                5. Maturity: Ready in 8 to 12 weeks when dark brown, crumbly, and smells like fresh soil.
            """.trimIndent(),
            actionableTips = listOf(
                "Keep compost pile shaded under trees or straw thatch to retain moisture.",
                "Add leguminous leaves (Leucaena, Calliandra) to boost nitrogen content.",
                "Apply 2 handfuls of mature compost per planting hole at sowing time."
            ),
            suitableCrops = listOf("All Vegetables", "Maize", "Fruit Trees", "Cassava")
        ),
        FarmingGuide(
            id = "guide_drip_irrigation",
            title = "Low-Cost Gravity Drip Irrigation Setup",
            category = "Water Management",
            summary = "Efficient water application directly to root zones, saving up to 60% water compared to furrow irrigation.",
            detailContent = """
                Drip irrigation delivers precise water doses to plant roots, minimizing weed growth and leaf disease risk.
                
                Gravity Drip Kit Components:
                1. Elevated Water Tank: 200L to 1000L barrel elevated 1.5 to 2 meters above ground level.
                2. Mesh Screen Filter: Essential to prevent emitters from clogging with silt or algae.
                3. Mainline Pipe: 16mm or 25mm HDPE pipe connected to control valve.
                4. Drip Tape Lines: 16mm drip lines with emitters spaced 20cm or 30cm apart.
                
                System Maintenance:
                - Flush Mainlines: Open pipe end-caps weekly to clear accumulated sediment.
                - Vinegar Soak: Clean clogged emitters by soaking in mild vinegar solution if mineral buildup occurs.
                - Mulching: Apply dry straw or grass mulch over drip lines to double water efficiency and lower soil temperature.
            """.trimIndent(),
            actionableTips = listOf(
                "Elevate water drum at least 1.5m to achieve sufficient water pressure.",
                "Irrigate early in the morning to minimize evaporation loss.",
                "Always filter water before filling the main supply drum."
            ),
            suitableCrops = listOf("Tomatoes", "Peppers", "Cabbage", "Kales", "Watermelon")
        ),
        FarmingGuide(
            id = "guide_farm_recordkeeping",
            title = "Simple Daily Farm Accounting for Profitability",
            category = "Business & Finance",
            summary = "Track input expenses, seasonal labor, harvest yields, and market sales to calculate true crop profit margins.",
            detailContent = """
                A farm is a business. Maintaining simple daily financial logs empowers women farmers to make smart planting decisions and qualify for agricultural loans.
                
                The Four Key Logs to Keep:
                1. Cash Inflow Register: Record every harvest sale date, quantity sold, unit price, and buyer name.
                2. Cash Outflow Register: Record every expenditure (seeds, land prep, fertilizer, transport, labor wages).
                3. Input Inventory Log: Track remaining bags of fertilizer, seed packs, and crop protection items.
                4. Labor Log: Track family vs paid casual labor hours for realistic cost estimation.
                
                Calculating Seasonal Net Profit:
                Gross Sales Revenue - Total Expenses = Net Profit/Loss
                If Net Profit > 0, calculate Profit Margin % = (Net Profit / Gross Revenue) * 100.
            """.trimIndent(),
            actionableTips = listOf(
                "Log all cash transactions immediately at the end of each day.",
                "Keep digital receipts or written receipts in a designated binder.",
                "Separate household personal expenses from farm business cash."
            ),
            suitableCrops = listOf("Commercial Crops", "Horticulture", "Poultry & Dairy")
        ),
        FarmingGuide(
            id = "guide_post_harvest",
            title = "Post-Harvest Drying & Hermetic Grain Storage",
            category = "Post-Harvest Care",
            summary = "Prevent aflatoxin mold and weevil damage using proper moisture testing and hermetic storage bags.",
            detailContent = """
                Post-harvest losses account for up to 30% of harvested grain. Proper drying and sealed storage protect crop value.
                
                Moisture Content Testing:
                - Salt Test Method: Place a sample of dry grain in a dry glass jar with 1 tablespoon dry table salt. Shake well. If salt sticks to jar walls after 15 minutes, moisture is above 13% and requires more drying.
                - Moisture Meter: Target 12.5% or lower for safe long-term grain storage.
                
                Hermetic Bag Usage Guidelines:
                1. Dry grain thoroughly on clean tarpaulins (never directly on soil).
                2. Winnow and clean out broken grains, dust, and chaff.
                3. Fill the inner plastic liners of hermetic bags (PICS or similar) and twist inner liner tightly to seal out oxygen.
                4. Tie securely with twine. Sealed oxygen-free environment starves weevils naturally without chemicals.
            """.trimIndent(),
            actionableTips = listOf(
                "Never store damp grain in hermetic bags to prevent toxic mold.",
                "Store filled grain bags on raised wooden pallets, away from walls.",
                "Keep storage rooms cool, dark, and well-ventilated."
            ),
            suitableCrops = listOf("Maize", "Beans", "Sorghum", "Cowpeas", "Rice")
        ),
        FarmingGuide(
            id = "guide_women_coops",
            title = "Women Farmers Cooperatives & Micro-Grants",
            category = "Funding & Networks",
            summary = "Leverage collective bargaining, table banking (Chama), and agricultural development grants.",
            detailContent = """
                Working in women-led farming groups boosts negotiating power, lowers transport costs, and opens access to affordable capital.
                
                Key Advantages of Cooperative Farming:
                - Bulk Purchasing: Combine seed and fertilizer orders to secure 10-15% bulk discounts from suppliers.
                - Aggregated Selling: Pool grain or produce volume to supply formal institutional buyers and supermarkets.
                - Table Banking (Chama Savings): Group members save small weekly amounts to create a revolving low-interest loan pool for seasonal farm inputs.
                - Grant Access: Agricultural grants from development funds usually require group registration with local social services.
            """.trimIndent(),
            actionableTips = listOf(
                "Register your farming group with local county/district social development offices.",
                "Maintain transparent, audited minute books and financial registers.",
                "Appoint designated marketing representatives to negotiate with buyers."
            ),
            suitableCrops = listOf("All Agricultural Ventures")
        )
    )

    private val guidesFlow = MutableStateFlow(sampleGuides)

    override fun getGuides(): Flow<List<FarmingGuide>> {
        return guidesFlow
    }

    override fun getGuideById(id: String): FarmingGuide? {
        return sampleGuides.find { it.id == id }
    }

    override fun searchGuides(query: String, category: String?): Flow<List<FarmingGuide>> {
        val normalizedQuery = query.trim()
        return guidesFlow.map { list ->
            list.filter { guide ->
                val matchesCategory = category.isNullOrBlank() || category == "All" || guide.category.equals(category, ignoreCase = true)
                val matchesQuery = normalizedQuery.isBlank() || guide.title.contains(normalizedQuery, ignoreCase = true) ||
                        guide.summary.contains(normalizedQuery, ignoreCase = true) ||
                        guide.detailContent.contains(normalizedQuery, ignoreCase = true) ||
                        guide.suitableCrops.any { it.contains(normalizedQuery, ignoreCase = true) }
                matchesCategory && matchesQuery
            }
        }
    }
}
