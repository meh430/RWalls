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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.imageBackgroundGradient

@Composable
fun SubredditDetail(
    modifier: Modifier = Modifier,
    description: String = "",
    headerImageUrl: String,
    iconImageUrl: String,
    subredditName: String,
    title: String,
    subTitle: String,
    isSaved: Boolean,
    onSaveChanged: (Boolean) -> Unit
) {
    OutlinedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            SubredditHeaderImage(
                modifier = Modifier.padding(bottom = 12.dp),
                headerImageUrl = headerImageUrl,
                iconImageUrl = iconImageUrl,
                subredditName = subredditName,
                title = title,
                subTitle = subTitle,
                isSaved = isSaved,
                onSaveChanged = onSaveChanged
            )
            if (description.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = description,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SubredditHeaderImage(
    modifier: Modifier = Modifier,
    headerImageUrl: String,
    iconImageUrl: String,
    subredditName: String,
    title: String,
    subTitle: String,
    isSaved: Boolean,
    onSaveChanged: (Boolean) -> Unit
) {
    val style = MaterialTheme.typography.bodyMedium.copy(
        shadow = Shadow(
            color = Color.Black,
            offset = Offset(x = 2f, y = 4f),
            blurRadius = 2f
        )
    )
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.Black
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                imageUrl = headerImageUrl
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(imageBackgroundGradient)
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Bottom
            ) {
                SubredditIcon(
                    size = 54.dp,
                    subredditIconUrl = iconImageUrl,
                    subredditName = subredditName
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = subredditName,
                        style = style.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        style = style,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subTitle,
                        style = style,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                SaveButton(isSaved = isSaved, onClick = onSaveChanged)
            }
        }
    }
}

@Preview
@Composable
fun SubredditDetailPreview() {
    var isSaved by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    var selectionState by remember { mutableStateOf(SelectionState.SELECTABLE) }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SubredditDetail(
                        modifier = Modifier.padding(8.dp),
                        subredditName = "r/animewallpaper",
                        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer accumsan, ante eu ullamcorper porttitor, dui dolor malesuada purus, sed fringilla mauris lorem quis augue. Proin commodo bibendum condimentum.",
                        headerImageUrl = "https://styles.redditmedia.com/t5_2ss60/styles/bannerBackgroundImage_x2tx5ryggi711.png?width=4000&s=69f12cf2d5d63fde1adc9820764f9cd1182436db",
                        iconImageUrl = "https://styles.redditmedia.com/t5_2ss60/styles/communityIcon_bx5asf07hi711.png?width=256&s=aa64b672872834f8d49bf3ae560543a9d9bd7ee6",
                        title = "252k members",
                        subTitle = "437 online",
                        isSaved = isSaved,
                        onSaveChanged = { isSaved = it }
                    )
                }
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