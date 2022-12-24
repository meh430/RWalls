package mp.redditwalls.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.Flow
import mp.redditwalls.design.components.ImageFolderRadioDialog
import mp.redditwalls.design.components.ImagePage
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.utils.toFriendlyCount

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImagePager(
    modifier: Modifier = Modifier,
    images: List<ImageItemUiState>,
    usePresetFolderWhenLiking: Boolean,
    folderNames: List<String>,
    navigateToPost: (ImageItemUiState) -> Unit,
    onImageSetWallpaperClick: (ImageItemUiState) -> Unit,
    onLoadMore: () -> Unit,
    onLikeClick: (ImageItemUiState, Boolean, String?) -> Unit
) {
    val pagerState = rememberPagerState()

    LaunchedEffect(pagerState) {
        val pageChanges: Flow<Int> = snapshotFlow { pagerState.currentPage }
        pageChanges.collect {
            if (images.size - it < 3) {
                onLoadMore()
            }
        }
    }

    var showFolderSelectDialog: ImageItemUiState? by remember { mutableStateOf(null) }

    ImageFolderRadioDialog(
        show = showFolderSelectDialog != null,
        options = folderNames,
        onSubmit = { name ->
            showFolderSelectDialog?.let {
                onLikeClick(it, !it.isLiked.value, name)
            }
        },
        onDismiss = { showFolderSelectDialog = null }
    )

    VerticalPager(
        modifier = modifier.fillMaxSize(),
        count = images.size,
        state = pagerState,
        key = { images.getOrNull(it)?.networkId.orEmpty() }
    ) { index ->
        images.getOrNull(index)?.let { image ->
            ImagePage(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                imageUrl = image.imageUrl.url,
                title = image.postTitle,
                subredditName = image.subredditName,
                authorName = image.author,
                isLiked = image.isLiked.value,
                numUpvotes = image.numUpvotes.toFriendlyCount(),
                numComments = image.numComments.toFriendlyCount(),
                openPost = { navigateToPost(image) },
                onLikeChanged = { isLiked ->
                    if (usePresetFolderWhenLiking || !isLiked) {
                        onLikeClick(image, isLiked, null)
                    } else {
                        showFolderSelectDialog = image
                    }
                },
                onSetWallpaperClick = { onImageSetWallpaperClick(image) },
                onInfoTextClick = { }
            )
        }
    }
}