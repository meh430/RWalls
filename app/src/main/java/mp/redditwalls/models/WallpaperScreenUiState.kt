package mp.redditwalls.models

import mp.redditwalls.domain.models.DetailedImageResult

data class WallpaperScreenUiState(
    val image: ImageItemUiState = ImageItemUiState(),
    val subreddit: SubredditItemUiState = SubredditItemUiState(),
    val folderName: String = "",
    val folderNames: List<String> = emptyList(),
    val usePresetFolderWhenLiking: Boolean = false,
    val shouldHideUi: Boolean = false,
    val uiResult: UiResult = UiResult.Loading()
)

fun WallpaperScreenUiState.hideUi() = copy(shouldHideUi = true)

fun WallpaperScreenUiState.showUi() = copy(shouldHideUi = false)

fun WallpaperScreenUiState.updateState(result: DetailedImageResult) = with(result) {
    copy(
        image = domainImage.toImageItemUiState(),
        subreddit = domainSubreddit.toSubredditItemUiState(),
        folderName = folderName,
        folderNames = folderNames,
        usePresetFolderWhenLiking = usePresetFolderWhenLiking,
        uiResult = UiResult.Success()
    )
}