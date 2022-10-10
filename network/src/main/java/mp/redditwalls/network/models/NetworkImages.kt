package mp.redditwalls.network.models

import com.google.gson.annotations.SerializedName

data class NetworkImages(
    val images: List<NetworkImage> = emptyList(),
    @SerializedName("after")
    val nextPageId: String
)
