package com.example.places

object FclassCategoryMapper {

    private val map: Map<String, String> = mapOf(
        "cafe" to "Cafe",
        "restaurant" to "Restaurant",
        "fast_food" to "Restaurant",
        "bar" to "Nightlife",
        "pub" to "Nightlife",
        "biergarten" to "Nightlife",
        "nightclub" to "Nightlife",
        "mall" to "Shop",
        "department_store" to "Shop",
        "supermarket" to "Shop",
        "convenience" to "Shop",
        "bakery" to "Shop",
        "butcher" to "Shop",
        "greengrocer" to "Shop",
        "clothes" to "Shop",
        "shoes" to "Shop",
        "gift" to "Shop",
        "books" to "Shop",
        "jewelry" to "Shop",
        "electronics" to "Shop",
        "hardware" to "Shop",
        "furniture" to "Shop",
        "sports" to "Shop",
        "toys" to "Shop",
        "beverages" to "Shop",
        "chemist" to "Shop",
        "optician" to "Shop",
        "hairdresser" to "Shop",
        "beauty" to "Shop",
        "travel_agency" to "Shop",
        "mobile_phone" to "Shop",
        "computer" to "Shop",
        "stationery" to "Shop",
        "variety_store" to "Shop",
        "alcohol" to "Shop",
        "tobacco" to "Shop",
        "newsagent" to "Shop",
        "kiosk" to "Shop",
        "florist" to "Shop"
    )

    fun categoryFor(fclass: String): String? = map[fclass]
}