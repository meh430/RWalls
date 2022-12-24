package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.Date
import mp.redditwalls.design.components.ImageCardModel
import mp.redditwalls.design.components.SelectionState
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.models.DomainImageUrl
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.local.enums.WallpaperLocation

data class ImageItemUiState(
    val postTitle: String = "",
    val subredditName: String = "",
    val author: String = "",
    val numUpvotes: Int = 0,
    val numComments: Int = 0,
    val postUrl: String = "",
    val networkId: String = "",
    val imageUrl: ImageUrl,
    val selectionState: MutableState<SelectionState> = mutableStateOf(SelectionState.NOT_SELECTABLE),
    val isLiked: MutableState<Boolean> = mutableStateOf(false),
    val isAlbum: Boolean = false
)

fun DomainImage.toImageItemUiState(url: DomainImageUrl? = null) = ImageItemUiState(
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    networkId = networkId,
    imageUrl = url?.toImageUrl() ?: domainImageUrls.first().toImageUrl(),
    isLiked = mutableStateOf(isLiked),
    isAlbum = isAlbum
)

fun DomainImage.toImageItemUiStates() = domainImageUrls.map { url ->
    toImageItemUiState(url)
}

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
    selectionState = selectionState.value,
    onLikeClick = onLikeClick,
    onClick = onClick,
    onSelect = { onClick() },
    onLongPress = onLongPress
)

fun ImageItemUiState.toDomainWallpaperRecentActivityItem(
    location: WallpaperLocation,
    refresh: Boolean = false
) = if (refresh) {
    DomainRecentActivityItem.DomainRefreshWallpaperActivityItem(
        dbId = 0,
        createdAt = Date(),
        subredditName = subredditName,
        domainImageUrl = DomainImageUrl(
            url = imageUrl.url,
            lowQualityUrl = imageUrl.lowQualityUrl,
            mediumQualityUrl = imageUrl.mediumQualityUrl,
            highQualityUrl = imageUrl.highQualityUrl
        ),
        imageNetworkId = networkId,
        wallpaperLocation = location
    )
} else {
    DomainRecentActivityItem.DomainSetWallpaperActivityItem(
        dbId = 0,
        createdAt = Date(),
        subredditName = subredditName,
        domainImageUrl = DomainImageUrl(
            url = imageUrl.url,
            lowQualityUrl = imageUrl.lowQualityUrl,
            mediumQualityUrl = imageUrl.mediumQualityUrl,
            highQualityUrl = imageUrl.highQualityUrl
        ),
        imageNetworkId = networkId,
        wallpaperLocation = location
    )
}

data class ImageUrl(
    val url: String = "",
    val lowQualityUrl: String = "",
    val mediumQualityUrl: String = "",
    val highQualityUrl: String = "",
)

fun DomainImageUrl.toImageUrl() = ImageUrl(
    url = url,
    highQualityUrl = highQualityUrl,
    mediumQualityUrl = mediumQualityUrl,
    lowQualityUrl = lowQualityUrl
)