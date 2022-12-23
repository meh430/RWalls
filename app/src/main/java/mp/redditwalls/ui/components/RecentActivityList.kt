package mp.redditwalls.ui.components

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mp.redditwalls.activities.SearchImagesActivity
import mp.redditwalls.activities.SearchImagesActivityArguments
import mp.redditwalls.models.RecentActivityItem

@Composable
fun RecentActivityList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    recentActivityItems: List<RecentActivityItem>,
    onClick: (RecentActivityItem) -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding
    ) {
        recentActivityListItems(
            modifier = Modifier.padding(16.dp),
            recentActivityItems = recentActivityItems,
            onClick = onClick,
            context = context
        )
    }
}

fun LazyListScope.recentActivityListItems(
    modifier: Modifier = Modifier,
    context: Context,
    recentActivityItems: List<RecentActivityItem>,
    onClick: (RecentActivityItem) -> Unit = { recentActivity ->
        when (recentActivity) {
            is RecentActivityItem.RefreshWallpaperActivityItem -> TODO()
            is RecentActivityItem.SearchAllActivityItem -> SearchImagesActivity.launch(
                context,
                SearchImagesActivityArguments(
                    query = recentActivity.query
                )
            )
            is RecentActivityItem.SearchSubredditActivityItem -> SearchImagesActivity.launch(
                context,
                SearchImagesActivityArguments(
                    query = recentActivity.query,
                    subreddit = recentActivity.subredditName
                )
            )
            is RecentActivityItem.SetWallpaperActivityItem -> TODO()
            is RecentActivityItem.VisitSubredditActivityItem -> SearchImagesActivity.launch(
                context,
                SearchImagesActivityArguments(
                    subreddit = recentActivity.subredditName
                )
            )
        }
    }
) {
    items(recentActivityItems) { recentActivityItem ->
        RecentActivityCard(
            modifier = modifier,
            recentActivityItem = recentActivityItem,
            onClick = { onClick(recentActivityItem) }
        )
    }
}
