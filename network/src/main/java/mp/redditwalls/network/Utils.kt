package mp.redditwalls.network

import com.google.gson.JsonObject

internal object Utils {
    fun String.cleanImageUrl() = replace(Constants.AMP, "")

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