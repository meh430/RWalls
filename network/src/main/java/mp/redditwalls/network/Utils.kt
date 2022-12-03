package mp.redditwalls.network

import com.google.gson.JsonObject

internal object Utils {
    fun String.cleanImageUrl() = replace(Constants.AMP, "")

    fun JsonObject.getString(key: String, default: String = "") = if (validField(key)) {
        get(key).asString!!
    } else {
        default
    }

    fun JsonObject.getInt(key: String, default: Int = 0) = if (validField(key)) {
        get(key).asInt
    } else {
        default
    }

    fun JsonObject.getLong(key: String, default: Long = 0) = if (validField(key)) {
        get(key).asLong
    } else {
        default
    }

    private fun JsonObject.validField(key: String) = has(key) && !get(key).isJsonNull

}