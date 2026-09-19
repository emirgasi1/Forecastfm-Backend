package com.example.database.table

import database.table.Users
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Outfits : Table("outfits") {
    val id = text("id")
    val userId = text("userId").references(Users.id)
    val imageUrl = text("imageUrl")
    val title = text("title")
    val weatherCondition = text("weatherCondition")
    val season = text("season")
    val likes = integer("likes").default(0)
    val createdAt = timestamp("createdAt")
    val storeName = text("storename").nullable()
    val storeAddress = text("storeaddress").nullable()
    val price = text("price").nullable()
    val storePhone = varchar("store_phone", 50).nullable()
    val productUrl = varchar("product_url", 500).nullable()


    override val primaryKey = PrimaryKey(id)
}