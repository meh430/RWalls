package mp.redditwalls.network.models

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import mp.redditwalls.network.Utils.cleanImageUrl
import mp.redditwalls.network.Utils.getInt
import mp.redditwalls.network.Utils.getLong
import mp.redditwalls.network.Utils.getString

data class NetworkSubreddit(
    @SerializedName("display_name")
    val name: String,
    @SerializedName("subscribers")
    val numSubscribers: Int,
    @SerializedName("active_user_count")
    val numOnline: Int,
    @SerializedName("public_description")
    val description: String,
    @SerializedName("community_icon")
    val subredditIconUrl: String = "",
    @SerializedName("banner_background_image")
    val headerImageUrl: String = "",
    @SerializedName("created_utc")
    val createdUtc: Long
) {
    companion object {
        fun fromJson(json: JsonObject): NetworkSubreddit =
            json.getAsJsonObject("data").run {
                NetworkSubreddit(
                    name = getString("display_name"),
                    numSubscribers = getInt("subscribers"),
                    numOnline = getInt("active_user_count"),
                    description = getString("public_description"),
                    subredditIconUrl = getString("community_icon").cleanImageUrl().ifBlank {
                        getString("icon_img").cleanImageUrl()
                    },
                    headerImageUrl = getString("banner_background_image").cleanImageUrl(),
                    createdUtc = getLong("created_utc")
                )
            }
    }
}