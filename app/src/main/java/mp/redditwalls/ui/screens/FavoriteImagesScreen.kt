package mp.redditwalls.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import mp.redditwalls.R
import mp.redditwalls.design.components.AddFolderDialog
import mp.redditwalls.design.components.ClickableTextItem
import mp.redditwalls.design.components.DeleteConfirmationDialog
import mp.redditwalls.design.components.ErrorState
import mp.redditwalls.design.components.FilterChipBar
import mp.redditwalls.design.components.IconText
import mp.redditwalls.design.components.ImageFolderRadioDialog
import mp.redditwalls.design.components.OptionsMenu
import mp.redditwalls.design.components.SubtitleSwitch
import mp.redditwalls.design.components.WallpaperLocationRadioDialog
import mp.redditwalls.local.enums.WallpaperLocation
import mp.redditwalls.local.enums.getString
import mp.redditwalls.local.models.DbImageFolder.Companion.DEFAULT_FOLDER_NAME
import mp.redditwalls.models.UiResult
import mp.redditwalls.ui.components.ImagesList
import mp.redditwalls.ui.components.onImageCardClick
import mp.redditwalls.utils.DownloadUtils
import mp.redditwalls.viewmodels.FavoriteImagesScreenViewModel

const val SELECT_ALL = 0
const val MOVE_TO = 1
const val DOWNLOAD = 2
const val DELETE = 3

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FavoriteImagesScreen(
    modifier: Modifier = Modifier,
    vm: FavoriteImagesScreenViewModel = viewModel(),
    downloadUtils: DownloadUtils
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val listState = rememberLazyGridState()

    val uiState = vm.uiState
    val uiResult = uiState.uiResult.value

    val menuOptions = remember {
        context.run {
            listOf(
                IconText(getString(R.string.select_all)),
                IconText(getString(R.string.move_to)),
                IconText(getString(R.string.download)),
                IconText(getString(R.string.delete))
            )
        }
    }
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val topBarColor = if (uiState.selecting.value) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    systemUiController.setSystemBarsColor(topBarColor)
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { uiState.showAddFolderDialog.value = true },
                expanded = expandedFab,
                icon = { Icon(Icons.Filled.Add, "add") },
                text = { Text("Add Folder") },
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.selecting.value) {
                                stringResource(
                                    R.string.selected_count,
                                    uiState.selectedCount.value
                                )
                            } else {
                                uiState.filter.value
                            },
                        )
                    }
                },
                navigationIcon = {
                    if (uiState.selecting.value) {
                        IconButton(
                            onClick = { vm.stopSelecting() }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                actions = {
                    if (uiState.selecting.value) {
                        OptionsMenu(
                            options = menuOptions,
                            onOptionSelected = {
                                when (it) {
                                    SELECT_ALL -> vm.selectAll()
                                    MOVE_TO -> uiState.showMoveDialog.value = true
                                    DELETE -> uiState.showDeleteDialog.value = true
                                    DOWNLOAD -> vm.downloadSelection(downloadUtils)
                                    else -> {}
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = topBarColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
        ) {
            AddFolderDialog(
                show = uiState.showAddFolderDialog.value,
                onConfirmButtonClick = { vm.createFolder(it) },
                onDismiss = { uiState.showAddFolderDialog.value = false }
            )
            ImageFolderRadioDialog(
                show = uiState.showMoveDialog.value,
                options = uiState.folderNames,
                onSubmit = { vm.moveSelectionTo(it) },
                onDismiss = { uiState.showMoveDialog.value = false }
            )
            DeleteConfirmationDialog(
                show = uiState.showDeleteDialog.value,
                onConfirm = { vm.deleteSelection() },
                onDismiss = { uiState.showDeleteDialog.value = false }
            )
            when (uiResult) {
                is UiResult.Error -> {
                    ErrorState(
                        errorMessage = uiResult.errorMessage
                            ?: stringResource(id = R.string.error_state_title),
                        onRetryClick = {
                            vm.setFilter(uiState.filter.value)
                        }
                    )
                }
                else -> {
                    ImagesList(
                        modifier = Modifier,
                        listState = listState,
                        images = uiState.images,
                        isLoading = uiResult is UiResult.Loading,
                        onClick = {
                            if (uiState.selecting.value) {
                                vm.selectImage(it)
                            } else {
                                onImageCardClick(context, it)
                            }
                        },
                        onImageLongPress = {
                            if (!uiState.selecting.value) {
                                vm.startSelecting(it)
                            }
                        },
                        onLikeClick = vm.favoriteImageViewModel::onLikeClick,
                        onLoadMore = {},
                        header = {
                            Column {
                                if (uiState.folderNames.isNotEmpty()) {
                                    FilterChipBar(
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        filters = uiState.folderNames.map { IconText(text = it) },
                                        selection = uiState.folderNames.indexOf(uiState.filter.value),
                                        onSelectionChanged = {
                                            vm.setFilter(uiState.folderNames[it])
                                        }
                                    )
                                    if (uiState.masterRefreshEnabled.value || uiState.filter.value != DEFAULT_FOLDER_NAME) {
                                        ImageFolderSettingsCard(
                                            folderName = uiState.filter.value,
                                            refreshEnabled = uiState.folderRefreshEnabled.value,
                                            masterRefreshEnabled = uiState.masterRefreshEnabled.value,
                                            refreshLocation = uiState.refreshLocation.value,
                                            onRefreshChanged = {
                                                vm.updateFolderSettings(
                                                    refreshEnabled = it
                                                )
                                            },
                                            onLocationChange = {
                                                vm.updateFolderSettings(
                                                    refreshLocation = it
                                                )
                                            },
                                            onDeleteClick = vm::deleteFolder
                                        )
                                    }
                                }
                            }
                        },
                        folderNames = uiState.folderNames,
                        usePresetFolderWhenLiking = uiState.usePresetFolderWhenLiking.value
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.setFilter(force = true)
    }
}

@Composable
private fun ImageFolderSettingsCard(
    folderName: String,
    refreshEnabled: Boolean,
    masterRefreshEnabled: Boolean,
    refreshLocation: WallpaperLocation,
    onRefreshChanged: (Boolean) -> Unit,
    onLocationChange: (WallpaperLocation) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WallpaperLocationRadioDialog(
                show = showDialog,
                onSubmit = { onLocationChange(WallpaperLocation.values()[it]) },
                onDismiss = { showDialog = false },
                initialSelection = refreshLocation.ordinal
            )
            if (masterRefreshEnabled) {
                SubtitleSwitch(
                    title = "Enable random periodic refresh for this folder",
                    subtitle = "Periodically refresh the system wallpaper with a random favorite image from this folder",
                    checked = refreshEnabled,
                    onCheckChanged = onRefreshChanged
                )
                AnimatedVisibility(refreshEnabled) {
                    ClickableTextItem(
                        title = "Refresh images on...",
                        subtitle = refreshLocation.getString(LocalContext.current),
                        onClick = { showDialog = true }
                    )
                }
            }
            AnimatedVisibility(folderName != DEFAULT_FOLDER_NAME) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onDeleteClick(folderName) }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete this folder")
                    }
                }
            }
        }
    }
}
