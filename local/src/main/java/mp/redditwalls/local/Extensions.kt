package mp.redditwalls.local

import mp.redditwalls.local.models.DbImage

fun String.getNetworkImageIdAndIndex() = if (contains("@")) {
    split("@").let { arr ->
        arr[0] to arr[1].toInt()
    }
} else {
    this to 0
}

fun String.getNetworkId() = getNetworkImageIdAndIndex().first

fun String.getImageIndex() = getNetworkImageIdAndIndex().second

fun String.buildDbImageId(index: Int = 0) = "$this@$index"

/**
 * Returns a mapping of network ids and all liked images in the album. Each group is sorted by index
 */
fun List<DbImage>.toNetworkIdToDbImageMap() = groupBy {
    it.id.getNetworkId()
}.map { (networkId, dbImageIds) ->
    networkId to dbImageIds.sortedBy { it.id.getImageIndex() }
}.toMap()

fun Map<String, List<DbImage>>.getFirstDbImageInAlbum(networkId: String) =
    get(networkId)?.firstOrNull()?.takeIf {
        it.id.getImageIndex() == 0
    }