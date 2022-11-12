package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import mp.redditwalls.design.components.ImageCardModel
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.models.DomainImageUrl

data class ImageItemUiState(
    val postTitle: String = "",
    val subredditName: String = "",
    val author: String = "",
    val numUpvotes: Int = 0,
    val numComments: Int = 0,
    val postUrl: String = "",
    val networkId: String = "",
    val imageUrl: ImageUrl,
    val isLiked: MutableState<Boolean> = mutableStateOf(false),
    val isAlbum: Boolean = false
)

fun DomainImage.toImageItemScreenState() = ImageItemUiState(
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    networkId = networkId,
    imageUrl = domainImageUrls.first().let {
        ImageUrl(
            url = it.url,
            highQualityUrl = it.highQualityUrl,
            mediumQualityUrl = it.mediumQualityUrl,
            lowQualityUrl = it.lowQualityUrl
        )
    },
    isLiked = mutableStateOf(isLiked),
    isAlbum = isAlbum
)

fun ImageItemUiState.toDomainImage() = DomainImage(
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    networkId = networkId,
    domainImageUrls = listOf(
        DomainImageUrl(
            url = imageUrl.url,
            lowQualityUrl = imageUrl.lowQualityUrl,
            mediumQualityUrl = imageUrl.mediumQualityUrl,
            highQualityUrl = imageUrl.highQualityUrl
        )
    ),
    isLiked = isLiked.value,
    isAlbum = isAlbum
)

fun ImageItemUiState.toImageCardModel(
    onLikeClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) = ImageCardModel(
    key = networkId,
    imageUrl = imageUrl.url,
    title = postTitle,
    subTitle = subredditName,
    isAlbum = isAlbum,
    isLiked = isLiked.value,
    onLikeClick = onLikeClick,
    onClick = onClick,
    onLongPress = onLongPress
)

data class ImageUrl(
    val url: String = "",
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val highQualityUrl: String = "",
)