package mp.redditwalls.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mp.redditwalls.R
import mp.redditwalls.design.components.SubredditCard
import mp.redditwalls.models.SubredditItemUiState
import mp.redditwalls.utils.toFriendlyCount

@Composable
fun SubredditsList(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(8.dp),
    subreddits: List<SubredditItemUiState>,
    onClick: (SubredditItemUiState) -> Unit,
    onSaveChanged: (SubredditItemUiState, Boolean) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding
    ) {
        subredditListItems(
            subreddits = subreddits,
            onSaveChanged = onSaveChanged,
            onClick = onClick
        )
    }
}

fun LazyListScope.subredditListItems(
    subreddits: List<SubredditItemUiState>,
    onSaveChanged: (SubredditItemUiState, Boolean) -> Unit,
    onClick: (SubredditItemUiState) -> Unit
) {
    items(subreddits) { subreddit ->
        SubredditCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            subredditIconUrl = subreddit.subredditIconUrl,
            subredditName = "r/${subreddit.name}",
            subscriberCount = stringResource(
                R.string.subscriber_count,
                subreddit.numSubscribers.toFriendlyCount()
            ),
            subredditDescription = subreddit.description,
            isSaved = subreddit.isSaved.value,
            onSaveChanged = { onSaveChanged(subreddit, it) },
            onClick = { onClick(subreddit) },
            onLongPress = {}
        )
    }
}