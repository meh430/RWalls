package mp.redditwalls.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mp.redditwalls.activities.WallpaperActivity
import mp.redditwalls.activities.WallpaperActivityArguments
import mp.redditwalls.design.components.EmptyState
import mp.redditwalls.design.components.ImageCard
import mp.redditwalls.design.components.ImageFolderRadioDialog
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.toImageCardModel
import mp.redditwalls.utils.OnBottomReached

@Composable
fun ImagesList(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    listState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    images: List<ImageItemUiState>,
    isLoading: Boolean,
    usePresetFolderWhenLiking: Boolean,
    folderNames: List<String>,
    onClick: (ImageItemUiState) -> Unit = { onImageCardClick(context, it) },
    onImageLongPress: (ImageItemUiState) -> Unit,
    onLikeClick: (image: ImageItemUiState, isLiked: Boolean, folder: String?) -> Unit,
    onLoadMore: () -> Unit,
    header: (@Composable () -> Unit)? = null
) {
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

    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.Center
    ) {
        if (header != null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                header()
            }
        }

        items(
            items = images,
            key = { it.networkId }
        ) {
            ImageCard(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(275.dp),
                imageCardModel = it.toImageCardModel(
                    onLikeClick = { isLiked ->
                        if (usePresetFolderWhenLiking || !isLiked) {
                            onLikeClick(it, isLiked, null)
                        } else {
                            showFolderSelectDialog = it
                        }
                    },
                    onClick = { onClick(it) },
                    onLongPress = { onImageLongPress(it) }
                )
            )
        }
        if (isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                    ThreeDotsLoader()
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                    )
                }
            }
        } else if (images.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(modifier = Modifier.padding(vertical = 48.dp))
            }
        } else {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                )
            }
        }
    }
    listState.OnBottomReached {
        onLoadMore()
    }
}

fun onImageCardClick(context: Context, image: ImageItemUiState) {
    WallpaperActivity.launch(
        context = context,
        arguments = WallpaperActivityArguments(
            imageUrl = image.imageUrl.highQualityUrl
        )
    )
}