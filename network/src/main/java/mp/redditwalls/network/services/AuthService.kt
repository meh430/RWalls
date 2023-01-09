package mp.redditwalls.network.services

import mp.redditwalls.network.Constants.DEVICE_ID
import mp.redditwalls.network.Constants.GRANT_TYPE
import mp.redditwalls.network.models.AuthResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @FormUrlEncoded
    @POST("/api/v1/access_token")
    suspend fun getAccessToken(
        @Field("grant_type") grantType: String = GRANT_TYPE,
        @Field("device_id") deviceId: String = DEVICE_ID
    ): AuthResponse
}