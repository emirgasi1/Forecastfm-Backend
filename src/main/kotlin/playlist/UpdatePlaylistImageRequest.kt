package playlist

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePlaylistImageRequest(
    val imageUrl: String
)