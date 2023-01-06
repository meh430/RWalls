package mp.redditwalls.network.models

import com.google.gson.annotations.SerializedName

data class NetworkImages(
    val images: List<NetworkImage> = emptyList(),
    @SerializedName("after")
    val nextPageId: String? = null
)

fun NetworkImages.filter(includeOver18: Boolean) = copy(
    images = images.filter { (it.isOver18 && includeOver18) || !it.isOver18 }
)
