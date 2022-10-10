package mp.redditwalls.network

import com.google.gson.JsonObject
import mp.redditwalls.network.models.ApiResponse
import retrofit2.Response

internal object Utils {
    fun String.cleanImageUrl() = replace(Constants.AMP, "")

    // Errors will be caught in flows
    fun <T> Response<T>.getData(): ApiResponse<T> = body()?.let {
        if (this.isSuccessful) {
            ApiResponse.Success(it)
        } else {
            null
        }
    } ?: ApiResponse.Error("Unable to fetch data")

    fun JsonObject.getString(value: String, default: String = "") = if (has(value)) {
        get(value).asString!!
    } else {
        default
    }

    fun JsonObject.getInt(value: String, default: Int = 0) = if (has(value)) {
        get(value).asInt
    } else {
        default
    }

    fun JsonObject.getLong(value: String, default: Long = 0) = if (has(value)) {
        get(value).asLong
    } else {
        default
    }

}