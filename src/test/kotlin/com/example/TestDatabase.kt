package com.example

import com.example.database.DatabaseFactory

object TestDatabase {

    private var initialized = false

    init {
        System.setProperty("DB_URL", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
        System.setProperty("DB_USER", "root")
        System.setProperty("DB_PASSWORD", "")
    }

    fun initOnce() {
        if (initialized) return
        DatabaseFactory.init()
        initialized = true
    }
}