package mp.redditwalls.models

import mp.redditwalls.domain.models.DetailedImageResult

data class WallpaperScreenUiState(
    val image: ImageItemUiState = ImageItemUiState(),
    val subreddit: SubredditItemUiState = SubredditItemUiState(),
    val folderName: String = "",
    val uiResult: UiResult = UiResult.Loading()
)

fun WallpaperScreenUiState.updateState(result: DetailedImageResult) = with(result) {
    copy(
        image = domainImage.toImageItemUiState(),
        subreddit = domainSubreddit.toSubredditItemUiState(),
        folderName = folderName,
        uiResult = UiResult.Success()
    )
}