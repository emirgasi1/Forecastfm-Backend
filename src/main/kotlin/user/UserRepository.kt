package com.example.user


import com.example.database.table.Users
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.Uuid

class UserRepository{
    fun createUser(
        username:String,
        bio:String?,
        profileImageUrl: String?,
        favoriteLocation: String?
    ):String{

        val id= Uuid.random()
        transaction {
            Users.insert {
                it[Users.id]=id
                it[Users.username]=username
                it[Users.bio]=bio
                it[Users.profileImageUrl]=profileImageUrl
                it[Users.favoriteLocation]=favoriteLocation
            }
        }
        return id.toString()

    }

    fun getUserById(id: Uuid): User? {
        return transaction {

            Users
                .selectAll()
                .where { Users.id eq id }
                .singleOrNull()
                ?.let { row ->
                    User(
                        id = row[Users.id].toString(),
                        username = row[Users.username],
                        bio = row[Users.bio],
                        profileImageUrl = row[Users.profileImageUrl],
                        favoriteLocation = row[Users.favoriteLocation]
                    )
                }
        }
    }

}