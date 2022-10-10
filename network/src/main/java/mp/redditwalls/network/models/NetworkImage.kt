package mp.redditwalls.network.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import mp.redditwalls.network.Constants
import mp.redditwalls.network.Utils.getInt
import mp.redditwalls.network.Utils.getLong
import mp.redditwalls.network.Utils.getString

data class NetworkImage(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val postTitle: String = "",
    @SerializedName("subreddit")
    val subredditName: String = "",
    @SerializedName("over_18")
    val isOver18: Boolean = false,
    @SerializedName("author")
    val author: String = "",
    @SerializedName("created_utc")
    val createdUtc: Long = 0,
    @SerializedName("ups")
    val numUpvotes: Int = 0,
    @SerializedName("num_comments")
    val numComments: Int = 0,
    @SerializedName("permalink")
    val postUrl: String = "",
    val galleryItems: List<GalleryItem> = emptyList()
) {
    companion object {
        fun fromJson(json: JsonObject): NetworkImage? {
            var galleryItems: List<GalleryItem>? = null
            if (json.has("media_metadata")) {
                val metadataJson = json.getAsJsonObject("media_metadata")
                galleryItems = metadataJson.keySet().map {
                    GalleryItem.fromGalleryJson(metadataJson.getAsJsonObject(it))
                }
            } else if (json.has("preview")) {
                galleryItems = listOf(
                    GalleryItem.fromPreviewJson(json.getAsJsonObject("preview"))
                )
            }
            return galleryItems?.let {
                NetworkImage(
                    id = "t3_" + json.getString("id"),
                    postTitle = json.getString("title"),
                    subredditName = json.getString("subreddit"),
                    author = json.getString("author"),
                    createdUtc = json.getLong("created_utc"),
                    numUpvotes = json.getInt("ups"),
                    numComments = json.getInt("num_comments"),
                    postUrl = Constants.BASE_MOBILE_URL + json.getString("permalink")
                )
            }
        }
    }
}