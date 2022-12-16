package mp.redditwalls.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mp.redditwalls.R
import mp.redditwalls.design.components.ClickableTextItem
import mp.redditwalls.design.components.RadioDialog
import mp.redditwalls.design.components.SubtitleSwitch
import mp.redditwalls.models.SettingsRadioDialogModel
import mp.redditwalls.viewmodels.SettingsScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(vm: SettingsScreenViewModel = viewModel()) {
    val uiState = vm.settingsScreenUiState
    val currentDialog = vm.settingsScreenUiState.currentRadioDialog.value

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                modifier = Modifier,
                scrollBehavior = scrollBehavior,
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.settings))
                    }
                },
                navigationIcon = {},
                actions = {}
            )
        }
    ) { innerPadding ->
        val horizontalPadding = 16.dp
        val spacerHeight = 12.dp
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
                .verticalScroll(scrollState)
        ) {
            currentDialog?.let {
                RadioDialog(
                    show = true,
                    title = stringResource(it.titleId),
                    selection = it.selection,
                    options = it.options.map { id -> stringResource(id) },
                    confirmButtonText = stringResource(R.string.ok),
                    cancelButtonText = stringResource(R.string.cancel),
                    onSelectionChanged = vm::changeDialogSelection,
                    onConfirmButtonClick = vm::onDialogConfirmClicked,
                    onDismiss = vm::dismissDialog
                )
            }
            // general
            SettingsSectionTitle(stringResource(R.string.general))
            Spacer(modifier = Modifier.height(spacerHeight))
            SubtitleSwitch(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(R.string.allow_nsfw_images),
                checked = uiState.allowNsfw.value,
                onCheckChanged = { vm.settingsScreenUiState.allowNsfw.value = it }
            )
            Spacer(modifier = Modifier.height(spacerHeight))
            ClickableTextItem(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(R.string.image_preview_resolution),
                subtitle = stringResource(uiState.previewResolution.value.stringId),
                onClick = { vm.showDialog(SettingsRadioDialogModel.PreviewResolutionDialog(uiState.previewResolution.value.ordinal)) }
            )
            Spacer(modifier = Modifier.height(spacerHeight))
            ClickableTextItem(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(R.string.theme),
                subtitle = stringResource(uiState.theme.value.stringId),
                onClick = { vm.showDialog(SettingsRadioDialogModel.ThemeDialog(uiState.theme.value.ordinal)) }
            )
            Spacer(modifier = Modifier.height(spacerHeight))

            // home
            SettingsSectionTitle(stringResource(R.string.home))
            Spacer(modifier = Modifier.height(spacerHeight))
            ClickableTextItem(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(R.string.default_home_screen_sort),
                subtitle = stringResource(uiState.defaultHomeSort.value.stringId),
                onClick = {
                    vm.showDialog(
                        SettingsRadioDialogModel.DefaultHomeScreenSortDialog(
                            uiState.defaultHomeSort.value.ordinal
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.height(spacerHeight))
            SubtitleSwitch(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(R.string.view_vertical_image_feed),
                subtitle = stringResource(R.string.view_vertical_image_feed_subtitle),
                checked = uiState.verticalSwipeFeedEnabled.value,
                onCheckChanged = { vm.settingsScreenUiState.verticalSwipeFeedEnabled.value = it }
            )
            Spacer(modifier = Modifier.height(spacerHeight))

            // refresh
            SettingsSectionTitle(stringResource(R.string.wallpaper_refresh))
            Spacer(modifier = Modifier.height(spacerHeight))
            SubtitleSwitch(
                modifier = Modifier.padding(horizontal = horizontalPadding),
                title = stringResource(R.string.refresh_favorite_images),
                subtitle = stringResource(R.string.refresh_favorite_images_subtitle),
                checked = uiState.refreshEnabled.value,
                onCheckChanged = { vm.settingsScreenUiState.refreshEnabled.value = it }
            )
            AnimatedVisibility(uiState.refreshEnabled.value) {
                Column {
                    Spacer(modifier = Modifier.height(spacerHeight))
                    ClickableTextItem(
                        modifier = Modifier.padding(horizontal = horizontalPadding),
                        title = stringResource(R.string.refresh_interval),
                        subtitle = stringResource(uiState.refreshInterval.value.stringId),
                        onClick = {
                            vm.showDialog(
                                SettingsRadioDialogModel.RefreshIntervalDialog(
                                    uiState.refreshInterval.value.ordinal
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(spacerHeight))
                    ClickableTextItem(
                        modifier = Modifier.padding(horizontal = horizontalPadding),
                        title = stringResource(R.string.allow_refresh_when_on),
                        subtitle = stringResource(uiState.dataSetting.value.stringId),
                        onClick = { vm.showDialog(SettingsRadioDialogModel.DataSettingDialog(uiState.dataSetting.value.ordinal)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacerHeight))
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall
    )
}
