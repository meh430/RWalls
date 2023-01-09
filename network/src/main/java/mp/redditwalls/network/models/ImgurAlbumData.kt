package mp.redditwalls.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurAlbumData(
    val id: String,
    @Json(name = "images_count")
    val imageCount: Int,
    val images: List<ImgurImage>
)
