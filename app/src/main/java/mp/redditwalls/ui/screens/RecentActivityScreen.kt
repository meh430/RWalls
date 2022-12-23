package mp.redditwalls.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mp.redditwalls.design.components.BackButton
import mp.redditwalls.design.components.DeleteConfirmationDialog
import mp.redditwalls.design.components.EmptyState
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.FilterChipBar
import mp.redditwalls.design.components.IconText
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.domain.RecentActivityFilter
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.recentActivityListItems
import mp.redditwalls.viewmodels.RecentActivityScreenViewModel

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun RecentActivityScreen(
    vm: RecentActivityScreenViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult.value

    val filters = remember {
        listOf("all", "wallpaper", "search", "browse").map {
            IconText(it)
        }
    }

    val listState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Recent Activity")
                    }
                },
                navigationIcon = {
                    BackButton(tint = MaterialTheme.colorScheme.onSurface) {
                        navController.popBackStack()
                    }
                },
                actions = {
                    IconButton(onClick = { uiState.showDeleteConfirmation.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .consumedWindowInsets(it)
        ) {
            DeleteConfirmationDialog(
                show = uiState.showDeleteConfirmation.value,
                onConfirm = { vm.deleteAllHistory() },
                onDismiss = { uiState.showDeleteConfirmation.value = false }
            )
            when (uiResult) {
                is UiResult.Error -> ErrorState(errorMessage = uiResult.errorMessage.orEmpty())
                is UiResult.Loading -> ThreeDotsLoader()
                is UiResult.Success -> LazyColumn(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    state = listState,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    stickyHeader(key = "recent_activity_filter") {
                        FilterChipBar(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(vertical = 8.dp),
                            filters = filters,
                            selection = uiState.filter.value.ordinal,
                            onSelectionChanged = { selection ->
                                vm.fetchRecentActivity(RecentActivityFilter.values()[selection])
                            }
                        )
                    }

                    if (uiState.recentActivity.isEmpty()) {
                        item(key = "empty_state") {
                            EmptyState()
                        }
                    }

                    uiState.recentActivity.forEach { (date, recentActivity) ->
                        item(key = date) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = date,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                BackButton(cross = true) {
                                    vm.deleteHistoryForDate(date)
                                }
                            }
                        }
                        recentActivityListItems(
                            modifier = Modifier.padding(8.dp),
                            context = context,
                            recentActivityItems = recentActivity
                        )
                        item(key = "$date spacer") {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }

            }
        }
    }

    LaunchedEffect(Unit) {
        vm.fetchRecentActivity(RecentActivityFilter.ALL)
    }
}