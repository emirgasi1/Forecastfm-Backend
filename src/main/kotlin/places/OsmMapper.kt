package com.example.places

internal fun OverpassElement.toOsmPlace(): OsmPlace? {
    val name = tags["name"] ?: return null
    val lat = lat ?: center?.lat ?: return null
    val lon = lon ?: center?.lon ?: return null

    val street = tags["addr:street"]
    val houseNumber = tags["addr:housenumber"]
    val address = listOfNotNull(street, houseNumber)
        .joinToString(" ")
        .takeIf { it.isNotBlank() }

    return OsmPlace(
        osmId = "$type/$id",
        name = name,
        latitude = lat,
        longitude = lon,
        address = address,
        phone = tags["phone"] ?: tags["contact:phone"],
        website = tags["website"] ?: tags["contact:website"]
    )
}