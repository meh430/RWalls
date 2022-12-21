package mp.redditwalls.network.models

import com.google.gson.JsonObject
import mp.redditwalls.network.Utils.cleanImageUrl
import mp.redditwalls.network.Utils.getString

data class GalleryItem(
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val sourceUrl: String = "",
) {
    companion object {
        private val imageUrlPattern by lazy {
            Regex("""^.*\.(jpg|png|jpeg)${'$'}""")
        }

        fun fromGalleryJson(json: JsonObject): GalleryItem {
            val sourceJson = json.getAsJsonObject("s")
            val resolutions = json.getAsJsonArray("p")
            return GalleryItem(
                lowQualityUrl = resolutions
                    .first()
                    .asJsonObject
                    .getString("u")
                    .cleanImageUrl(),
                mediumQualityUrl = resolutions
                    .last()
                    .asJsonObject
                    .getString("u")
                    .cleanImageUrl(),
                sourceUrl = sourceJson.getString("u").cleanImageUrl()
            )
        }

        fun fromPreviewJson(json: JsonObject): GalleryItem {
            val imageJson = json.getAsJsonArray("images").get(0).asJsonObject
            val sourceJson = imageJson.getAsJsonObject("source")
            val resolutions = imageJson.getAsJsonArray("resolutions")
            return GalleryItem(
                lowQualityUrl = resolutions
                    .first()
                    .asJsonObject
                    .getString("url")
                    .cleanImageUrl(),
                mediumQualityUrl = resolutions
                    .last()
                    .asJsonObject
                    .getString("url")
                    .cleanImageUrl(),
                sourceUrl = sourceJson.getString("url").cleanImageUrl()
            )
        }

        fun fromUrl(url: String) = GalleryItem(
            lowQualityUrl = url,
            mediumQualityUrl = url,
            sourceUrl = url
        ).takeIf { imageUrlPattern.matches(url) }
    }
}