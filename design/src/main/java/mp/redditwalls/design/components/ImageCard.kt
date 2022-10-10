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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.imageBackgroundGradient
import mp.redditwalls.design.secondaryWhite

@Composable
fun ImageCard(
    imageUrl: String,
    title: String,
    subTitle: String,
    isLiked: Boolean,
    onLikeClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
    val iconTint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSecondaryContainer
    ElevatedCard(
        modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
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
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            )
                            .size(38.dp),
                        onClick = { onLikeClicked(!isLiked) },
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryWhite,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

        }

    }
}

@Preview
@Composable
fun ImageCardPreview() {
    var isLiked by remember { mutableStateOf(false) }
    RwTheme {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(8.dp),
        ) {
            items(5) {
                ImageCard(
                    imageUrl = "https://htmlcolorcodes.com/assets/images/colors/red-color-solid-background-1920x1080.png",
                    title = "Cool Wallpaper",
                    subTitle = "r/wallpaper",
                    isLiked = isLiked,
                    onLikeClicked = { newValue ->
                        isLiked = newValue
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(275.dp)
                )
            }
        }
    }
}