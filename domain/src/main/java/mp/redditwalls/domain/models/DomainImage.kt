package mp.redditwalls.domain.models

import mp.redditwalls.domain.Utils.getImageUrl
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.network.models.NetworkImage
import mp.redditwalls.preferences.enums.ImageQuality

data class DomainImage(
    val postTitle: String = "",
    val subredditName: String = "",
    val author: String = "",
    val numUpvotes: Int = 0,
    val numComments: Int = 0,
    val postUrl: String = "",
    val networkId: String = "",
    val dbId: Int = 0,
    val domainImageUrls: List<DomainImageUrl> = emptyList(),
    val isLiked: Boolean = false,
    val isAlbum: Boolean = false
)

data class DomainImageUrl(
    val url: String = "", // url to use based on prefs
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val highQualityUrl: String = "",
)

fun NetworkImage.toDomainImage(
    previewResolution: ImageQuality,
    isLiked: Boolean,
    dbId: Int
) = DomainImage(
    networkId = id,
    dbId = dbId,
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    domainImageUrls = galleryItems.map {
        previewResolution.getImageUrl(
            it.lowQualityUrl,
            it.mediumQualityUrl,
            it.sourceUrl
        )
    },
    isLiked = isLiked,
    isAlbum = galleryItems.size > 1
)

fun DbImage.toDomainImage(
    previewResolution: ImageQuality,
    isLiked: Boolean
) = DomainImage(
    postTitle = postTitle,
    subredditName = subredditName,
    postUrl = postUrl,
    networkId = networkId,
    dbId = id,
    domainImageUrls = listOf(
        previewResolution.getImageUrl(
            lowQualityUrl,
            mediumQualityUrl,
            sourceUrl
        )
    ),
    isLiked = isLiked
)
