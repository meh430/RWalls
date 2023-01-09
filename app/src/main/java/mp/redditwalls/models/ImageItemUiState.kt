package mp.redditwalls.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.Date
import mp.redditwalls.design.components.ImageCardModel
import mp.redditwalls.design.components.SelectionState
import mp.redditwalls.domain.models.DomainImage
import mp.redditwalls.domain.models.DomainRecentActivityItem
import mp.redditwalls.domain.models.ImageId
import mp.redditwalls.domain.models.ImageUrl
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.network.Constants

data class ImageItemUiState(
    val postTitle: String = "",
    val subredditName: String = "",
    val author: String = "",
    val numUpvotes: Int = 0,
    val numComments: Int = 0,
    val postUrl: String = "",
    val imageId: ImageId = ImageId(),
    val imageUrl: ImageUrl = ImageUrl(),
    val folderName: String = "",
    val selectionState: MutableState<SelectionState> = mutableStateOf(SelectionState.NOT_SELECTABLE),
    val isLiked: MutableState<Boolean> = mutableStateOf(false),
    val isAlbum: Boolean = false,
    val isNsfw: Boolean = false,
    val createdAt: Date = Date()
) {
    val authorUrl: String
        get() = "${Constants.BASE_REDDIT_URL}/u/$author"
}

fun DomainImage.toImageItemUiState() = ImageItemUiState(
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    imageId = imageId,
    imageUrl = imageUrls.first(),
    isLiked = mutableStateOf(isLiked),
    folderName = folderName,
    isAlbum = isAlbum,
    createdAt = createdAt,
    isNsfw = isNsfw
)

fun ImageItemUiState.toDomainImage() = DomainImage(
    postTitle = postTitle,
    subredditName = subredditName,
    author = author,
    numUpvotes = numUpvotes,
    numComments = numComments,
    postUrl = postUrl,
    imageId = imageId,
    imageUrls = listOf(imageUrl),
    folderName = folderName,
    isLiked = isLiked.value,
    isAlbum = isAlbum,
    isNsfw = isNsfw
)

fun ImageItemUiState.toImageCardModel(
    onLikeClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) = ImageCardModel(
    key = imageId.dbImageId,
    imageUrl = imageUrl.url,
    title = postTitle,
    subTitle = "r/${subredditName}",
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
        imageUrl = imageUrl,
        imageId = imageId,
        wallpaperLocation = location
    )
} else {
    DomainRecentActivityItem.DomainSetWallpaperActivityItem(
        dbId = 0,
        createdAt = Date(),
        subredditName = subredditName,
        imageUrl = imageUrl,
        imageId = imageId,
        wallpaperLocation = location
    )
}
