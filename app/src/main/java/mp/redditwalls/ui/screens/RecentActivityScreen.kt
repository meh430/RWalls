package mp.redditwalls.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mp.redditwalls.design.components.BackButton
import mp.redditwalls.design.components.EmptyState
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.ThreeDotsLoader
import mp.redditwalls.domain.RecentActivityFilter
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.recentActivityListItems
import mp.redditwalls.viewmodels.RecentActivityScreenViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecentActivityScreen(
    vm: RecentActivityScreenViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    val uiState = vm.uiState
    val uiResult = vm.uiState.uiResult.value

    val listState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Recent Activity")
                    }
                },
                navigationIcon = {
                    BackButton {
                        navController.popBackStack()
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
            when (uiResult) {
                is UiResult.Error -> ErrorState(errorMessage = uiResult.errorMessage.orEmpty())
                is UiResult.Loading -> ThreeDotsLoader()
                is UiResult.Success -> if (uiState.recentActivity.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                        state = listState,
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        uiState.recentActivity.forEach { (date, recentActivity) ->
                            item(key = date) {
                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = date,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleLarge
                                )
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
    }

    LaunchedEffect(Unit) {
        vm.fetchRecentActivity(RecentActivityFilter.ALL)
    }
}