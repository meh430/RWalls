package mp.redditwalls.network.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import javax.inject.Inject
import mp.redditwalls.network.models.NetworkImage
import mp.redditwalls.network.models.NetworkImages

class NetworkImagesDeserializer @Inject constructor() : JsonDeserializer<NetworkImages> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NetworkImages {
        val jsonObject = json.asJsonObject
        val data = jsonObject.getAsJsonObject("data")
        val children = data.getAsJsonArray("children")
        val images = children.map {
            val childData = it.asJsonObject.getAsJsonObject("data")
            NetworkImage.fromJson(childData)
        }
        return NetworkImages(
            images = images.filterNotNull(),
            nextPageId = data.get("after")?.asString.orEmpty()
        )
    }

}