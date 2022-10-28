package mp.redditwalls.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurAlbumResponse(
    val success: Boolean,
    val status: Int,
    val data: ImgurAlbumData
)
