package mp.redditwalls.domain.models

import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize
import mp.redditwalls.domain.Utils.getImageUrl
import mp.redditwalls.local.buildDbImageId
import mp.redditwalls.local.getNetworkImageIdAndIndex
import mp.redditwalls.local.models.DbImage
import mp.redditwalls.network.models.GalleryItem
import mp.redditwalls.network.models.NetworkImage
import mp.redditwalls.preferences.enums.ImageQuality

data class DomainImage(
    val postTitle: String = "",
    val subredditName: String = "",
    val author: String = "",
    val numUpvotes: Int = 0,
    val numComments: Int = 0,
    val postUrl: String = "",
    val imageId: ImageId,
    val imageUrls: List<ImageUrl> = emptyList(),
    val isLiked: Boolean = false,
    val folderName: String,
    val isAlbum: Boolean = false,
    val createdAt: Date = Date()
)

data class ImageUrl(
    val url: String = "", // url to use based on prefs
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val highQualityUrl: String = "",
)

fun GalleryItem.toImageUrl(previewResolution: ImageQuality) = previewResolution.getImageUrl(
    lowQualityUrl,
    mediumQualityUrl,
    sourceUrl
)
fun List<GalleryItem>.toImageUrls(previewResolution: ImageQuality) = map {
    it.toImageUrl(previewResolution)
}

@Parcelize
data class ImageId(
    val networkId: String,
    val index: Int // image index in gallery
) : Parcelable {
    val dbImageId: String
        get() = networkId.buildDbImageId(index)

    constructor(dbImageId: String = "") : this(
        dbImageId.getNetworkImageIdAndIndex().first,
        dbImageId.getNetworkImageIdAndIndex().second
    )
}

fun NetworkImage.toDomainImage(
    previewResolution: ImageQuality,
    dbImage: DbImage?
) = DomainImage(
    imageId = dbImage?.id?.let { ImageId(it) } ?: ImageId(id) ,
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    imageUrls = galleryItems.toImageUrls(previewResolution),
    isLiked = dbImage != null,
    folderName = dbImage?.imageFolderName.orEmpty(),
    isAlbum = galleryItems.size > 1 || imgurAlbumId.isNotEmpty(),
    createdAt = Date(createdUtc * 1000)
)


fun NetworkImage.toDomainImages(
    previewResolution: ImageQuality,
    dbImageSelector: (ImageId) -> DbImage?
) = galleryItems.mapIndexed { index, item ->
    val dbImageId = ImageId(id, index)
    val dbImage = dbImageSelector(dbImageId)
    toDomainImage(previewResolution, dbImage).copy(
        imageUrls = listOf(item.toImageUrl(previewResolution)),
        imageId = dbImageId
    )
}

fun DbImage.toDomainImage(previewResolution: ImageQuality) = DomainImage(
    postTitle = postTitle,
    subredditName = subredditName,
    postUrl = postUrl,
    imageId = ImageId(id),
    imageUrls = listOf(
        previewResolution.getImageUrl(
            lowQualityUrl,
            mediumQualityUrl,
            sourceUrl
        )
    ),
    isLiked = true,
    folderName = imageFolderName
)
