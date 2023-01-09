package mp.redditwalls.models

import mp.redditwalls.domain.models.DetailedImageResult

data class WallpaperScreenUiState(
    val images: List<ImageItemUiState> = emptyList(),
    val subreddit: SubredditItemUiState = SubredditItemUiState(),
    val folderNames: List<String> = emptyList(),
    val usePresetFolderWhenLiking: Boolean = false,
    val shouldHideUi: Boolean = false,
    val uiResult: UiResult = UiResult.Loading()
)

fun WallpaperScreenUiState.hideUi() = copy(shouldHideUi = true)

fun WallpaperScreenUiState.showUi() = copy(shouldHideUi = false)

fun WallpaperScreenUiState.updateState(result: DetailedImageResult) = with(result) {
    this@updateState.copy(
        uiResult = UiResult.Success(),
        images = images.map { it.toImageItemUiState() },
        subreddit = domainSubreddit.toSubredditItemUiState(),
        folderNames = folderNames,
        usePresetFolderWhenLiking = usePresetFolderWhenLiking
    )
}