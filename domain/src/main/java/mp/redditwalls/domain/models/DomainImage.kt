package mp.redditwalls.domain.models

import java.util.Date
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
    val domainImageUrls: List<DomainImageUrl> = emptyList(),
    val isLiked: Boolean = false,
    val isAlbum: Boolean = false,
    val createdAt: Date = Date()
)

data class DomainImageUrl(
    val url: String = "", // url to use based on prefs
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val highQualityUrl: String = "",
)

fun NetworkImage.toDomainImage(
    previewResolution: ImageQuality,
    isLiked: Boolean
) = DomainImage(
    networkId = id,
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
    isAlbum = galleryItems.size > 1,
    createdAt = Date(createdUtc * 1000)
)

fun DbImage.toDomainImage(
    previewResolution: ImageQuality,
    isLiked: Boolean
) = DomainImage(
    postTitle = postTitle,
    subredditName = subredditName,
    postUrl = postUrl,
    networkId = networkId,
    domainImageUrls = listOf(
        previewResolution.getImageUrl(
            lowQualityUrl,
            mediumQualityUrl,
            sourceUrl
        )
    ),
    isLiked = isLiked
)
