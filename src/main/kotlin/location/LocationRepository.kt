package com.example.location

import com.example.database.table.Locations
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class LocationRepository {

    fun getLocations(): List<Location> = transaction {

        Locations
            .selectAll()
            .map {
                Location(
                    id = it[Locations.id].toString(),
                    name = it[Locations.name],
                    description = it[Locations.description],
                    latitude = it[Locations.latitude],
                    longitude = it[Locations.longitude]
                )
            }
    }
}