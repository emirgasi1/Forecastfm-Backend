package com.example.database.table

import database.table.Users
import org.jetbrains.exposed.v1.core.Table

object SavedOutfits : Table("saved_outfits") {
    val userId = text("userId").references(Users.id)
    val outfitId = text("outfitId").references(Outfits.id)

    override val primaryKey = PrimaryKey(userId, outfitId)
}