package mp.redditwalls.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.imageBackgroundGradient

@Composable
fun SubredditDetail(
    modifier: Modifier = Modifier,
    description: String,
    headerImageUrl: String,
    iconImageUrl: String,
    title: String,
    subTitle: String,
    isSaved: Boolean,
    onSaveChanged: (Boolean) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        SubredditHeaderImage(
            modifier = Modifier.padding(bottom = 12.dp),
            headerImageUrl = headerImageUrl,
            iconImageUrl = iconImageUrl,
            title = title,
            subTitle = subTitle,
            isSaved = isSaved,
            onSaveChanged = onSaveChanged
        )
        Text(
            modifier = Modifier.padding(4.dp),
            text = description,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SubredditHeaderImage(
    modifier: Modifier = Modifier,
    headerImageUrl: String,
    iconImageUrl: String,
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
                SubredditIcon(subredditIconUrl = iconImageUrl)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        style = style,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                        .size(38.dp),
                    onClick = { onSaveChanged(!isSaved) }
                ) {
                    Icon(
                        imageVector = if (isSaved) {
                            Icons.Default.Bookmark
                        } else {
                            Icons.Default.BookmarkBorder
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SubredditDetailPreview() {
    var isSaved by remember { mutableStateOf(false) }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SubredditDetail(
                modifier = Modifier.padding(12.dp),
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer accumsan, ante eu ullamcorper porttitor, dui dolor malesuada purus, sed fringilla mauris lorem quis augue. Proin commodo bibendum condimentum.",
                headerImageUrl = "https://styles.redditmedia.com/t5_2ss60/styles/bannerBackgroundImage_x2tx5ryggi711.png?width=4000&s=69f12cf2d5d63fde1adc9820764f9cd1182436db",
                iconImageUrl = "https://styles.redditmedia.com/t5_2ss60/styles/communityIcon_bx5asf07hi711.png?width=256&s=aa64b672872834f8d49bf3ae560543a9d9bd7ee6",
                title = "252k members",
                subTitle = "437 online",
                isSaved = isSaved,
                onSaveChanged = { isSaved = it }
            )
        }
    }
}