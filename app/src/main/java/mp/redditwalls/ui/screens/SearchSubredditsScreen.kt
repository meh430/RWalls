package mp.redditwalls.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import mp.redditwalls.R
import mp.redditwalls.design.components.SearchBar
import mp.redditwalls.design.components.TextRecentActivityCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchSubredditsScreen() {
    val context = LocalContext.current

    val searchBarFocusRequester = remember { FocusRequester() }
    Scaffold(
        topBar = {
            SearchBar(
                value = "",
                onValueChanged = {},
                onSearch = {},
                hint = stringResource(R.string.search),
                onIconClick = { (context as? Activity)?.finish() },
                showBackButton = true,
                focusRequester = searchBarFocusRequester
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumedWindowInsets(it)
                .padding(it)
        ) {
            // search all images
            item {
                TextRecentActivityCard(
                    modifier = Modifier.padding(
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    icon = Icons.Default.Search,
                    title = getSearchAllString("query"),
                    date = "",
                    onClick = {}
                )
            }

            // search history when launched or subreddits when not
        }
    }

    LaunchedEffect(searchBarFocusRequester) {
        delay(200)
        searchBarFocusRequester.requestFocus()
    }
}

private fun getSearchAllString(query: String) = "Search for all images " + if (query.isNotBlank()) {
    "with '$query'"
} else {
    ""
}