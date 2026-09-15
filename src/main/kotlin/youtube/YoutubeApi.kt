package com.example.youtube

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode

class YouTubeApi(
    private val client: HttpClient
) {
    private val apiKey: String = System.getenv("YOUTUBE_API_KEY") ?: ""
    suspend fun getPlaylistItems(playlistId: String, maxResults: Int = 50): List<YouTubePlaylistItem> {
        if (apiKey.isBlank()) {
            throw Exception("YouTube API key not set")
        }

        val response = client.get {
            url("https://www.googleapis.com/youtube/v3/playlistItems")
            parameter("part", "snippet")
            parameter("playlistId", playlistId)
            parameter("maxResults", maxResults)
            parameter("key", apiKey)
        }

        if (response.status != HttpStatusCode.OK) {
            throw Exception("YouTube API error: ${response.status}")
        }

        val body: YouTubePlaylistItemsResponse = response.body()
        return body.items ?: emptyList()
    }
    suspend fun getVideoDurations(videoIds: List<String>): Map<String, Int> {
        if (videoIds.isEmpty()) return emptyMap()
        if (apiKey.isBlank()) return emptyMap()

        val result = mutableMapOf<String, Int>()

        videoIds.chunked(50).forEach { chunk ->
            val response = client.get {
                url("https://www.googleapis.com/youtube/v3/videos")
                parameter("part", "contentDetails")
                parameter("id", chunk.joinToString(","))
                parameter("key", apiKey)
            }

            if (response.status == HttpStatusCode.OK) {
                val body: YouTubeVideosResponse = response.body()
                body.items?.forEach { item ->
                    val seconds = parseIsoDuration(item.contentDetails?.duration ?: "")
                    result[item.id] = seconds
                }
            }
        }

        return result
    }

    private fun parseIsoDuration(iso: String): Int {
        if (iso.isBlank()) return 0
        val regex = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
        val match = regex.find(iso) ?: return 0
        val hours = match.groupValues[1].toIntOrNull() ?: 0
        val minutes = match.groupValues[2].toIntOrNull() ?: 0
        val seconds = match.groupValues[3].toIntOrNull() ?: 0
        return hours * 3600 + minutes * 60 + seconds
    }


}