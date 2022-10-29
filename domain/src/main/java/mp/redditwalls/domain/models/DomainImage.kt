package mp.redditwalls.domain.models

import mp.redditwalls.local.models.DbImage
import mp.redditwalls.network.models.NetworkImage

data class DomainImage(
    val postTitle: String = "",
    val subredditName: String = "",
    val author: String = "",
    val numUpvotes: Int = 0,
    val numComments: Int = 0,
    val postUrl: String = "",
    val networkId: String = "",
    val dbId: Int = 0,
    val imageUrl: String = "",
    val isLiked: Boolean = false,
    val isAlbum: Boolean = false
)

fun NetworkImage.toDomainImage(
    imageUrl: String,
    isLiked: Boolean,
    dbId: Int
) = DomainImage(
    networkId = id,
    dbId = dbId,
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    postUrl = postUrl,
    imageUrl = imageUrl,
    isLiked = isLiked,
    isAlbum = imgurAlbumId.isNotEmpty()
)

fun DbImage.toDomainImage(
    imageUrl: String,
    isLiked: Boolean
) = DomainImage(
    postTitle = postTitle,
    subredditName = subredditName,
    postUrl = postUrl,
    networkId = networkId,
    dbId = id,
    imageUrl = imageUrl,
    isLiked = isLiked
)
