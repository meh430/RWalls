package mp.redditwalls.network.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import javax.inject.Inject
import mp.redditwalls.network.Utils.getString
import mp.redditwalls.network.models.NetworkSubreddit
import mp.redditwalls.network.models.NetworkSubreddits

class NetworkSubredditsDeserializer @Inject constructor() : JsonDeserializer<NetworkSubreddits> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NetworkSubreddits {
        val children = json.asJsonObject
            .getAsJsonObject("data")
            .getAsJsonArray("children")
        val subreddits = children.filter { child ->
            !child.isJsonNull &&
                    child.isJsonObject &&
                    child.asJsonObject.getString("kind") == "t5"
        }.map { NetworkSubreddit.fromJson(it.asJsonObject) }
        return NetworkSubreddits(
            subreddits = subreddits
        )
    }

}