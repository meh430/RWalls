package mp.redditwalls.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.util.UUID
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.imageBackgroundGradient
import mp.redditwalls.design.secondaryWhite

data class ImageCardModel(
    val key: String = UUID.randomUUID().toString(),
    val imageUrl: String,
    val title: String,
    val subTitle: String,
    val isAlbum: Boolean = false,
    val isLiked: Boolean,
    val onLikeClick: (Boolean) -> Unit,
    val onClick: () -> Unit,
    val onLongPress: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    imageCardModel: ImageCardModel
) {
    val icon = if (imageCardModel.isLiked) {
        Icons.Filled.Favorite
    } else {
        Icons.Filled.FavoriteBorder
    }
    val iconTint = if (imageCardModel.isLiked) {
        Color.Red
    } else {
        Color.White
    }
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
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                imageCardModel.onLikeClick(true)
                                playAnimation = true
                            },
                            onLongPress = { imageCardModel.onLongPress() }
                        )
                    },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageCardModel.imageUrl)
                    .crossfade(true)
                    .build(),
                loading = { CircularProgressIndicator() },
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                contentDescription = null,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (imageCardModel.isAlbum) {
                        Arrangement.SpaceBetween
                    } else {
                        Arrangement.End
                    }
                ) {
                    if (imageCardModel.isAlbum) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            tint = Color.White,
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .clickable { imageCardModel.onLikeClick(!imageCardModel.isLiked) }
                            .size(24.dp),
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint
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
                            isAlbum = true,
                            isLiked = isLiked,
                            onLikeClick = { newValue ->
                                isLiked = newValue
                            },
                            onClick = {},
                            onLongPress = {}
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