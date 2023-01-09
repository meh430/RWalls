package mp.redditwalls.network.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import javax.inject.Inject
import mp.redditwalls.network.models.NetworkSubreddit

class NetworkSubredditDeserializer @Inject constructor() : JsonDeserializer<NetworkSubreddit> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ) = NetworkSubreddit.fromJson(json.asJsonObject)
}