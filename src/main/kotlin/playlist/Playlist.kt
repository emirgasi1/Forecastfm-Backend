package playlist

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: String,
    val title: String,
    val genre: String,
    val mood: String,
    val albumImageUrl: String? = null,
    val weather: String,
    val temperature: String,
    val location: String,
    val likes: Int = 0,
    val spotifyUrl: String? = null,
    val youtubeUrl: String? = null,
    val bestFor: List<String> = emptyList()
)