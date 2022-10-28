package mp.redditwalls.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.imageBackgroundGradient
import mp.redditwalls.design.secondaryWhite

@Composable
fun ImagePage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    subredditName: String,
    authorName: String,
    isLiked: Boolean,
    numUpvotes: String,
    numComments: String,
    openPost: () -> Unit,
    onLikeChanged: (Boolean) -> Unit,
    onSetWallpaperClick: () -> Unit,
    onLongPress: () -> Unit
) {
    var playAnimation by remember { mutableStateOf(false) }
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                onLikeChanged(true)
                                playAnimation = true
                            },
                            onLongPress = { onLongPress() }
                        )
                    },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
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
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                InfoText(
                    modifier = Modifier
                        .padding(bottom = 20.dp, start = 20.dp)
                        .weight(1f),
                    title = title,
                    subredditName = subredditName,
                    authorName = authorName,
                    onClick = onLongPress
                )
                IconColumn(
                    modifier = Modifier.padding(20.dp),
                    isLiked = isLiked,
                    numUpvotes = numUpvotes,
                    numComments = numComments,
                    openPost = openPost,
                    onLikeChanged = onLikeChanged,
                    onSetWallpaperClick = onSetWallpaperClick
                )
            }
            LikeAnimation(
                modifier = Modifier.fillMaxSize(),
                show = playAnimation,
                onAnimationComplete = { playAnimation = false }
            )
        }
    }
}

@Composable
fun InfoText(
    modifier: Modifier = Modifier,
    title: String,
    subredditName: String,
    authorName: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subredditName,
            style = MaterialTheme.typography.bodyLarge,
            color = secondaryWhite,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = authorName,
            style = MaterialTheme.typography.bodyLarge,
            color = secondaryWhite,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Icon Bar
// upvotes, comments, like, info icon
@Composable
fun IconColumn(
    modifier: Modifier = Modifier,
    isLiked: Boolean,
    numUpvotes: String,
    numComments: String,
    openPost: () -> Unit,
    onLikeChanged: (Boolean) -> Unit,
    onSetWallpaperClick: () -> Unit,
) {
    val iconSize = 32.dp
    val icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
    val iconTint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSecondaryContainer
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = { onLikeChanged(!isLiked) },
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                tint = iconTint,
                contentDescription = null,
            )
        }
        IconButton(
            onClick = { onSetWallpaperClick() },
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = Icons.Default.Wallpaper,
                contentDescription = null,
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = openPost,
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                )
            }
            Text(numUpvotes)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = openPost,
            ) {
                Icon(
                    modifier = Modifier.size(iconSize),
                    imageVector = Icons.Default.Comment,
                    contentDescription = null,
                )
            }
            Text(numComments)
        }
    }
}


@Preview
@Composable
fun ImagePagePreview() {
    var isLiked by remember { mutableStateOf(false) }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ImagePage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                imageUrl = "https://w0.peakpx.com/wallpaper/1000/9/HD-wallpaper-star-wars-material-minimalism.jpg",
                title = LoremIpsum(words = 15).values.first(),
                subredditName = "r/wallpaper",
                authorName = "u/author",
                isLiked = isLiked,
                numComments = "1.2k",
                numUpvotes = "16.8k",
                openPost = {},
                onLikeChanged = { isLiked = it },
                onSetWallpaperClick = {},
                onLongPress = {}
            )
        }
    }
}