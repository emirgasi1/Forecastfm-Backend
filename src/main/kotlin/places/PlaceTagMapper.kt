package com.example.places

object PlaceTagMapper {

    fun suitableFor(category: String): List<String> {
        return when (category.lowercase().trim()) {
            "cafe" -> listOf("Alone", "Partner", "Friends", "Colleagues")
            "restaurant" -> listOf("Partner", "Family", "Friends")
            "shop", "shopping" -> listOf("Alone", "Partner", "Friends")
            "nightlife", "nightclub", "bar", "pub" -> listOf("Friends", "Colleagues")
            "park", "outdoor", "viewpoint" -> listOf("Family", "Friends", "Alone")
            "attraction", "culture" -> listOf("Family", "Partner", "Alone")
            "activity" -> listOf("Friends", "Family")
            else -> listOf("Alone", "Partner", "Friends", "Family")
        }
    }

    fun weatherCondition(category: String): String {
        return when (category.lowercase().trim()) {
            "park", "outdoor", "viewpoint" -> "Sunny"
            else -> "Any"
        }
    }

    fun ageGroup(category: String): List<String> {
        return when (category.lowercase().trim()) {
            "nightlife", "nightclub", "bar", "pub" -> listOf("Young Adults", "Adults")
            else -> listOf("All Ages")
        }
    }
}