package com.example.busstation

import com.example.database.table.BusStations
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID

class BusStationRepository {

    fun getAllBusStations(): List<BusStation> {
        return transaction {
            BusStations
                .selectAll()
                .map { row ->
                    BusStation(
                        id = row[BusStations.id],
                        name = row[BusStations.name],
                        latitude = row[BusStations.latitude],
                        longitude = row[BusStations.longitude],
                        lines = row[BusStations.lines].split(",").filter { it.isNotBlank() },
                        createdAt = row[BusStations.createdAt].toString()
                    )
                }
        }
    }

    fun getBusStationById(id: String): BusStation? {
        return transaction {
            BusStations
                .selectAll()
                .where { BusStations.id eq id }
                .singleOrNull()
                ?.let { row ->
                    BusStation(
                        id = row[BusStations.id],
                        name = row[BusStations.name],
                        latitude = row[BusStations.latitude],
                        longitude = row[BusStations.longitude],
                        lines = row[BusStations.lines].split(",").filter { it.isNotBlank() },
                        createdAt = row[BusStations.createdAt].toString()
                    )
                }
        }
    }

    fun createBusStation(
        name: String,
        latitude: Double,
        longitude: Double,
        lines: List<String>
    ): BusStation {
        val id = UUID.randomUUID().toString()
        val createdAt = Instant.now().toString()

        transaction {
            BusStations.insert {
                it[BusStations.id] = id
                it[BusStations.name] = name
                it[BusStations.latitude] = latitude
                it[BusStations.longitude] = longitude
                it[BusStations.lines] = lines.joinToString(",")
                it[BusStations.createdAt] = Instant.now()
            }
        }

        return BusStation(
            id = id,
            name = name,
            latitude = latitude,
            longitude = longitude,
            lines = lines,
            createdAt = createdAt
        )
    }
}