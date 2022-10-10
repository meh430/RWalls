package mp.redditwalls.network.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    val scope: String
)
