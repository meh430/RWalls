package mp.redditwalls.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mp.redditwalls.models.RecentActivityItem

@Composable
fun RecentActivityList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    recentActivityItems: List<RecentActivityItem>,
    onClick: (RecentActivityItem) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding
    ) {
        recentActivityListItems(
            recentActivityItems = recentActivityItems,
            onClick = onClick
        )
    }
}

fun LazyListScope.recentActivityListItems(
    recentActivityItems: List<RecentActivityItem>,
    onClick: (RecentActivityItem) -> Unit
) {
    items(recentActivityItems) {
        RecentActivityCard(
            modifier = Modifier.padding(
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            recentActivityItem = it,
            onClick = { onClick(it) }
        )
    }
}
