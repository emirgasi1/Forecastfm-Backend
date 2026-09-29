package com.example.music

import com.example.TestDataCleaner
import com.example.TestDatabase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MusicRepositoryTest {

    private val repository = MusicRepository()

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
    }

    @Test
    fun `createMusic inserts music entry into database and returns Music object`() {
        val music = repository.createMusic(
            title = "Midnight City",
            artist = "M83",
            duration = 243,
            albumImageUrl = "https://example.com/cover.jpg"
        )

        assertNotNull(music.id)
        assertEquals("Midnight City", music.title)
        assertEquals("M83", music.artist)
        assertEquals(243, music.duration)
        assertEquals("https://example.com/cover.jpg", music.albumImageUrl)
    }

    @Test
    fun `getMusicById returns correct music when it exists`() {
        val created = repository.createMusic(
            title = "Starboy",
            artist = "The Weeknd",
            duration = 230,
            albumImageUrl = null
        )

        val fetched = repository.getMusicById(created.id)

        assertNotNull(fetched)
        assertEquals(created.id, fetched.id)
        assertEquals("Starboy", fetched.title)
        assertEquals("The Weeknd", fetched.artist)
        assertEquals(230, fetched.duration)
        assertNull(fetched.albumImageUrl)
    }

    @Test
    fun `getMusicById returns null when music does not exist`() {
        val fetched = repository.getMusicById("non-existent-id")
        assertNull(fetched)
    }
}