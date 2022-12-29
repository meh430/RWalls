package mp.redditwalls.local

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
fun List<DbImage>.toNetworkIdToDbImageMap() = associate {
it.id.getNetworkId() to it
}*/
