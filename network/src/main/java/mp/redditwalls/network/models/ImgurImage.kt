package mp.redditwalls.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurImage(
    val id: String,
    val title: String,
    val size: Int, // size in bytes
    val link: String
)
