package mp.redditwalls.ui.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import mp.redditwalls.design.components.EmptyState
import mp.redditwalls.design.components.ImageCard
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.models.ImageItemUiState
import mp.redditwalls.models.toImageCardModel

@Composable
fun ImagesList(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    images: List<ImageItemUiState>,
    isLoading: Boolean,
    onClick: (ImageItemUiState) -> Unit,
    onImageLongPress: (ImageItemUiState) -> Unit,
    onLikeClick: (ImageItemUiState, Boolean) -> Unit,
    onLoadMore: () -> Unit,
    header: (@Composable () -> Unit)? = null
) {
    val listState = rememberLazyGridState()
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
                    onLikeClick = { isLiked -> onLikeClick(it, isLiked) },
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

@Composable
fun LazyGridState.OnBottomReached(
    buffer: Int = 6,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }.filter { it }.collect {
            onLoadMore()
        }
    }
}