package com.example.youtube

import kotlinx.serialization.Serializable

@Serializable
data class YouTubePlaylistItemsResponse(
    val items: List<YouTubePlaylistItem>? = null
)

@Serializable
data class YouTubePlaylistItem(
    val snippet: YouTubeSnippet? = null
)

@Serializable
data class YouTubeSnippet(
    val title: String = "",
    val videoOwnerChannelTitle: String? = null,
    val resourceId: YouTubeResourceId? = null,
    val thumbnails: YouTubeThumbnails? = null
)

@Serializable
data class YouTubeResourceId(
    val videoId: String = ""
)

@Serializable
data class YouTubeThumbnails(
    val medium: YouTubeThumbnail? = null,
    val high: YouTubeThumbnail? = null
)

@Serializable
data class YouTubeThumbnail(
    val url: String = ""
)

@Serializable
data class YouTubeVideoResponse(
    val id: String = "",
    val snippet: YouTubeSnippet? = null,
    val contentDetails: YouTubeContentDetails? = null
)

@Serializable
data class YouTubeContentDetails(
    val duration: String = ""
)

@Serializable
data class YouTubeVideosResponse(
    val items: List<YouTubeVideoResponse>? = null
)

@Serializable
data class YouTubeEnrichedItem(
    val videoId: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val thumbnailUrl: String
)