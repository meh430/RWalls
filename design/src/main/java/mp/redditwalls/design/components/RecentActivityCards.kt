package mp.redditwalls.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.RwTheme
import mp.redditwalls.design.imageBackgroundGradient
import mp.redditwalls.design.secondaryWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageRecentActivityCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    subTitle: String,
    date: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.Black
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                imageUrl = imageUrl
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(imageBackgroundGradient)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
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
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryWhite,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date,
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

@Composable
fun TextRecentActivityCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subTitle: String = "",
    date: String,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(32.dp),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
        Spacer(Modifier.width(12.dp))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subTitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            /**
            Spacer(modifier = Modifier.height(2.dp))
            if (date.isNotEmpty()) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
             */
        }
    }
}

@Preview
@Composable
fun RecentActivityPreview() {
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.padding(PaddingValues(8.dp))
            ) {
                item {
                    ImageRecentActivityCard(
                        imageUrl = "",
                        title = "Set on home screen",
                        subTitle = "r/wallpaper",
                        date = "4:06 PM · 10 Oct 22",
                        onClick = {},
                        modifier = Modifier.padding(8.dp)
                    )
                }
                item {
                    TextRecentActivityCard(
                        icon = Icons.Default.Search,
                        title = "Searched images with 'query'",
                        subTitle = "in r/wallpaper",
                        date = "4:06 PM · 10 Oct 22",
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(8.dp)
                    )
                }
                item {
                    TextRecentActivityCard(
                        icon = Icons.Default.Search,
                        title = "Searched images with 'query'",
                        subTitle = "",
                        date = "4:06 PM · 10 Oct 22",
                        onClick = {},
                        modifier = Modifier.padding(8.dp)
                    )
                }
                item {
                    ImageRecentActivityCard(
                        imageUrl = "https://static.wikia.nocookie.net/starwars/images/d/dd/Attack-Clones-Poster.jpg/revision/latest?cb=20180318125654",
                        title = "Refreshed on home screen",
                        subTitle = "r/wallpaper",
                        date = "4:06 PM · 10 Oct 22",
                        onClick = {},
                        modifier = Modifier.padding(8.dp)
                    )
                }
                item {
                    TextRecentActivityCard(
                        icon = Icons.Default.Explore,
                        title = "Browsed images in r/wallpaper'",
                        subTitle = "",
                        date = "4:06 PM · 10 Oct 22",
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}