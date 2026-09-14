package com.example.youtube

object YouTubeUtils {

    fun extractPlaylistId(url: String?): String? {
        if (url.isNullOrBlank()) return null



        return try {
            val uri = java.net.URI(url)
            val query = uri.query ?: return null
            query.split("&")
                .firstOrNull { it.startsWith("list=") }
                ?.removePrefix("list=")
        } catch (e: Exception) {
            null
        }
    }
}