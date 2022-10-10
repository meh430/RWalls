package mp.redditwalls.network.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import javax.inject.Inject
import mp.redditwalls.network.models.NetworkImage

class NetworkImageDeserializer @Inject constructor(
    private val networkImagesDeserializer: NetworkImagesDeserializer
) : JsonDeserializer<NetworkImage> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ) = networkImagesDeserializer.deserialize(json, typeOfT, context).images.first()
}