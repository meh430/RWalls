package com.example.redditwalls.fragments


import  android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.work.*
import com.example.redditwalls.R
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.misc.*
import com.example.redditwalls.repositories.ColumnCount
import com.example.redditwalls.repositories.RefreshInterval
import com.example.redditwalls.repositories.SettingsItem
import com.example.redditwalls.repositories.Theme
import com.example.redditwalls.viewmodels.SettingsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsComposeFragment : Fragment() {

    companion object {
        private const val REPO_LINK = "https://github.com/meh430/RedditWalls-rewrite"
    }

    @Inject
    lateinit var toaster: Toaster

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val darkColors = darkColors(
        primary = Color.Red,
        primaryVariant = Color.Red,
        secondary = Color.Red,
        secondaryVariant = Color.Red
    )
    private val lightColors = lightColors(
        primary = Color.Red,
        primaryVariant = Color.Red,
        secondary = Color.Red,
        secondaryVariant = Color.Red
    )
    private val sidesPadding = 14.dp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val currentDefault = settingsViewModel.getDefaultSub()
        val tips = resources.getStringArray(R.array.tips).joinToString(separator = "\n")
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(if (isSystemInDarkTheme()) darkColors else lightColors) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = sidesPadding)
                    ) {
                        OptionSelector(
                            "Select Theme",
                            settingsViewModel.getTheme(),
                            Theme.values()
                        ) {
                            settingsViewModel.setTheme(it)
                            AppCompatDelegate.setDefaultNightMode(it.mode)
                        }
                        Spacer(modifier = Modifier.height(sidesPadding))
                        OptionSelector(
                            dialogTitle = "Select Default Sort",
                            currentSelection = settingsViewModel.getDefaultSort(),
                            options = RWApi.Sort.values()
                        ) {
                            settingsViewModel.setDefaultSort(it)
                        }
                        Spacer(modifier = Modifier.height(sidesPadding))
                        ColumnCountSlider()
                        Toggles()
                        Spacer(modifier = Modifier.height(sidesPadding))
                        HomeFeedSetting(currentDefault)
                        Spacer(modifier = Modifier.height(sidesPadding))
                        RandomRefreshSetting()
                        Tips(tips)
                        TB("History") {
                            val toHistory =
                                SettingsComposeFragmentDirections.actionNavigationSettingsComposeToHistoryFragment()
                            findNavController().navigate(toHistory)
                        }
                        Spacer(modifier = Modifier.height(sidesPadding))
                        TB("Github") {
                            REPO_LINK.launchBrowser(requireActivity())
                        }
                        Spacer(modifier = Modifier.height(sidesPadding))
                    }

                }
            }
        }
    }

    @Composable
    fun DarkText(
        text: String,
        modifier: Modifier = Modifier,
        fontSize: TextUnit = TextUnit.Unspecified,
        fontWeight: FontWeight? = null,
        style: TextStyle = LocalTextStyle.current,
        darkTheme: Boolean = isSystemInDarkTheme()
    ) {
        Text(
            text,
            modifier = modifier,
            fontSize = fontSize,
            fontWeight = fontWeight,
            style = style,
            color = getTextColor(darkTheme)
        )
    }

    @Composable
    fun Toggles() {
        Spacer(modifier = Modifier.height(sidesPadding))
        TextSwitch("Allow NSFW results", settingsViewModel.nsfwAllowed()) {
            settingsViewModel.setNsfwAllowed(it)
        }
        Spacer(modifier = Modifier.height(sidesPadding))
        TextSwitch("Load low resolution previews", settingsViewModel.loadLowResPreviews()) {
            settingsViewModel.setLoadLowResPreviews(it)
        }
        Spacer(modifier = Modifier.height(sidesPadding))
        TextSwitch("Enable image opening animation", settingsViewModel.getAnimationsEnabled()) {
            settingsViewModel.setAnimationsEnabled(it)
        }
        Spacer(modifier = Modifier.height(sidesPadding))
        TextSwitch("Enable toast messages", settingsViewModel.toastEnabled()) {
            settingsViewModel.setToastEnabled(it)
        }
    }

    @Composable
    fun ColumnCountSlider() {
        var swipeEnabled by rememberSaveable { mutableStateOf(settingsViewModel.swipeEnabled()) }
        var count by rememberSaveable { mutableStateOf(settingsViewModel.getColumnCount().count.toFloat()) }

        val onClick: (Boolean) -> Unit = { value: Boolean ->
            swipeEnabled = value
            settingsViewModel.setSwipeEnabled(value)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Restart app?")
                .setMessage("Changing feed style requires app restart")
                .setPositiveButton("Yes") { _, _ ->
                    Utils.triggerRebirth(requireContext())
                }
                .setNegativeButton("No") { _, _ ->
                    swipeEnabled = !value
                    settingsViewModel.setSwipeEnabled(!value)
                }.show()
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .clickable { onClick(!swipeEnabled) }
                .fillMaxWidth()
        ) {
            DarkText("Enable swipeable home feed")
            Switch(checked = swipeEnabled, onCheckedChange = onClick)
        }

        if (!swipeEnabled) {
            Spacer(modifier = Modifier.height(sidesPadding))
            DarkText(text = "Set column count: ${count.toInt()} columns")
            Slider(
                value = count,
                onValueChange = { count = it },
                onValueChangeFinished = { settingsViewModel.setColumnCount(ColumnCount.fromId(count.toInt() - 1)) },
                steps = 2,
                valueRange = 1f..4f,
            )
        }
    }

    @Composable
    fun HomeFeedSetting(default: String) {
        var specifyHome by rememberSaveable { mutableStateOf(settingsViewModel.specifyHome()) }

        TextSwitch("Use specified default home sub", specifyHome) {
            settingsViewModel.setSpecifyHome(it)
            specifyHome = it
        }
        if (specifyHome) {
            DarkText(
                text = "Currently default is r/$default",
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Light
            )
        }
    }


    @Composable
    fun RandomRefreshSetting() {
        // switch, option for interval, location, and toggle for random order
        var refreshEnabled by rememberSaveable { mutableStateOf(settingsViewModel.randomRefreshEnabled()) }
        var randomOrder by rememberSaveable { mutableStateOf(settingsViewModel.randomOrder()) }

        SectionDivider("Wallpaper Refresh")
        TextSwitch("Enable periodic wallpaper refresh", refreshEnabled) {
            settingsViewModel.setRandomRefresh(it)
            refreshEnabled = it
        }

        if (refreshEnabled) {
            Spacer(modifier = Modifier.height(sidesPadding))
            TextSwitch("Refresh favorites in random order", randomOrder) {
                settingsViewModel.setRandomOrder(it)
                randomOrder = it
            }
            Spacer(modifier = Modifier.height(sidesPadding))

            if (!randomOrder) {
                TB("Reset refresh position") {
                    settingsViewModel.setRefreshIndex(0)
                    toaster.t("Successfully reset position")
                }
                Spacer(modifier = Modifier.height(sidesPadding))
            }

            OptionSelector(
                "Select refresh interval",
                settingsViewModel.getRandomRefreshInterval(),
                RefreshInterval.values()
            ) {
                settingsViewModel.setRandomRefreshInterval(it)
            }
            Spacer(modifier = Modifier.height(sidesPadding))
            OptionSelector(
                "Select wallpaper location",
                settingsViewModel.getRandomRefreshLocation(),
                WallpaperLocation.values()
            ) {
                settingsViewModel.setRandomRefreshLocation(it)
            }
        }
        Spacer(modifier = Modifier.height(sidesPadding))
    }

    @Composable
    fun Tips(tips: String) {
        SectionDivider(label = "Tips")
        DarkText(text = tips)
        Spacer(modifier = Modifier.height(sidesPadding))
    }

    @Composable
    fun SectionDivider(label: String) {
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(sidesPadding))
        Text(
            text = label,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(sidesPadding))
    }

    @Composable
    fun <T : SettingsItem> OptionSelector(
        dialogTitle: String,
        currentSelection: T,
        options: Array<T>,
        onSelect: (T) -> Unit
    ) {
        var selected by rememberSaveable { mutableStateOf(currentSelection) }
        val modifier = Modifier
            .clickable {
                RadioDialog(
                    requireContext(),
                    dialogTitle,
                    options,
                    selected.id
                ).show {
                    options
                        .fromId(it, options[0])
                        .also { o ->
                            selected = o
                            onSelect(o)
                        }
                }
            }
            .fillMaxWidth()
        Column(modifier = modifier) {
            DarkText(dialogTitle, style = MaterialTheme.typography.subtitle1)
            DarkText(
                selected.displayText,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Light
            )

        }
    }

    @Composable
    fun TextSwitch(
        text: String = "",
        checked: Boolean = true,
        onChange: (Boolean) -> Unit
    ) {
        var isChecked by rememberSaveable { mutableStateOf(checked) }
        val onClick = { value: Boolean ->
            isChecked = value
            onChange(value)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .clickable { onClick(!isChecked) }
                .fillMaxWidth()
        ) {
            DarkText(text)
            Switch(checked = isChecked, onCheckedChange = onClick)
        }
    }

    @Composable
    fun TB(label: String, onClick: () -> Unit) {
        TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(label)
        }
    }

    private fun getTextColor(darkTheme: Boolean) = if (darkTheme) Color.White else Color.Black

    override fun onPause() {
        super.onPause()

        if (settingsViewModel.randomRefreshEnabled() && settingsViewModel.randomRefreshSettingsChanged()) {
            settingsViewModel.location = settingsViewModel.getRandomRefreshLocation()
            settingsViewModel.interval = settingsViewModel.getRandomRefreshInterval()
            val interval = settingsViewModel.getRandomRefreshInterval()
            startRandomRefreshWork(interval)
        } else if (!settingsViewModel.randomRefreshEnabled() && settingsViewModel.randomRefreshEnabledChanged()) {
            cancelRandomRefreshWork()
            settingsViewModel.clearRandomRefreshSettings()
        }
    }

    private fun startRandomRefreshWork(interval: RefreshInterval) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<RandomRefreshWorker>(
            interval.amount,
            interval.timeUnit
        ).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.enqueueUniquePeriodicWork(
            SettingsFragment.RANDOM_REFRESH_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            work
        )
        toaster.t("Start refresh")
    }

    private fun cancelRandomRefreshWork() {
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.cancelUniqueWork(SettingsFragment.RANDOM_REFRESH_WORK)
    }
}