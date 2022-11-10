package mp.redditwalls.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.UUID
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.imageBackgroundGradient
import mp.redditwalls.design.secondaryWhite

enum class SelectionState {
    NOT_SELECTABLE,
    SELECTABLE,
    SELECTED
}

data class ImageCardModel(
    val key: String = UUID.randomUUID().toString(),
    val imageUrl: String,
    val title: String,
    val subTitle: String,
    val isAlbum: Boolean = false,
    val selectionState: SelectionState = SelectionState.NOT_SELECTABLE,
    val isLiked: Boolean,
    val onLikeClick: (Boolean) -> Unit,
    val onSelect: (SelectionState) -> Unit = {},
    val onClick: () -> Unit,
    val onLongPress: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    imageCardModel: ImageCardModel
) {
    var playAnimation by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        onClick = imageCardModel.onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.Black
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                imageUrl = imageCardModel.imageUrl,
                onDoubleTap = {
                    imageCardModel.onLikeClick(true)
                    playAnimation = true
                },
                onLongPress = { imageCardModel.onLongPress() }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(imageBackgroundGradient)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val showSelectionIndicator =
                    imageCardModel.selectionState != SelectionState.NOT_SELECTABLE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (imageCardModel.isAlbum || showSelectionIndicator) {
                        Arrangement.SpaceBetween
                    } else {
                        Arrangement.End
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (imageCardModel.isAlbum) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            tint = Color.White,
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                        )
                    } else if (showSelectionIndicator) {
                        CircleCheckbox(
                            checked = imageCardModel.selectionState == SelectionState.SELECTED,
                            onCheckedChange = {
                                val state = if (it) {
                                    SelectionState.SELECTED
                                } else {
                                    SelectionState.SELECTABLE
                                }
                                imageCardModel.onSelect(state)
                            }
                        )
                    }
                    LikeButton(
                        modifier = Modifier.size(24.dp),
                        isLiked = imageCardModel.isLiked,
                        onLikeClick = imageCardModel.onLikeClick
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = imageCardModel.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = imageCardModel.subTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryWhite,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            LikeAnimation(
                modifier = Modifier.fillMaxSize(),
                show = playAnimation,
                onAnimationComplete = { playAnimation = false }
            )
        }
    }
}

@Preview
@Composable
fun ImageCardPreview() {
    var isLiked by remember { mutableStateOf(false) }
    var selectionState by remember { mutableStateOf(SelectionState.SELECTABLE) }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(6) {
                    ImageCard(
                        imageCardModel = ImageCardModel(
                            imageUrl = "https://c4.wallpaperflare.com/wallpaper/165/600/954/iphone-ios-ipad-ipod-wallpaper-preview.jpg",
                            title = "Cool Wallpaper",
                            subTitle = "r/wallpaper",
                            isAlbum = false,
                            selectionState = selectionState,
                            isLiked = isLiked,
                            onLikeClick = { newValue ->
                                isLiked = newValue
                            },
                            onClick = {},
                            onLongPress = {},
                            onSelect = {
                                selectionState = it
                            }
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .height(275.dp)
                    )
                }
            }
        }
    }
}