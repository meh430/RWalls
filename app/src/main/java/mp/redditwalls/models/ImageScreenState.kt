package mp.redditwalls.models

import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.models.DomainImageUrl

data class ImageScreenState(
    val postTitle: String = "",
    val subredditName: String = "",
    val author: String = "",
    val numUpvotes: Int = 0,
    val numComments: Int = 0,
    val postUrl: String = "",
    val networkId: String = "",
    val dbId: Int = 0,
    val imageUrl: ImageUrl,
    val isLiked: Boolean = false,
    val isAlbum: Boolean = false
)

fun DomainImage.toImageItemScreenState() = ImageScreenState(
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    networkId = networkId,
    dbId = dbId,
    imageUrl = domainImageUrls.first().let {
        ImageUrl(
            url = it.url,
            highQualityUrl = it.highQualityUrl,
            mediumQualityUrl = it.mediumQualityUrl,
            lowQualityUrl = it.lowQualityUrl
        )
    },
    isLiked = isLiked,
    isAlbum = isAlbum
)

fun ImageScreenState.toDomainImage() = DomainImage(
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    networkId = networkId,
    dbId = dbId,
    domainImageUrls = listOf(
        DomainImageUrl(
            url = imageUrl.url,
            lowQualityUrl = imageUrl.lowQualityUrl,
            mediumQualityUrl = imageUrl.mediumQualityUrl,
            highQualityUrl = imageUrl.highQualityUrl
        )
    ),
    isLiked = isLiked,
    isAlbum = isAlbum
)

data class ImageUrl(
    val url: String = "",
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val highQualityUrl: String = "",
)