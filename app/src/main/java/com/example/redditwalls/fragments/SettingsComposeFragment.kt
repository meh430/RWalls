package com.example.redditwalls.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.misc.RadioDialog
import com.example.redditwalls.misc.fromId
import com.example.redditwalls.repositories.ColumnCount
import com.example.redditwalls.repositories.SettingsItem
import com.example.redditwalls.repositories.Theme
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsComposeFragment : Fragment() {

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
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(if (isSystemInDarkTheme()) darkColors else lightColors) {
                    Column(modifier = Modifier.padding(horizontal = sidesPadding)) {
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
                    }

                }
            }
        }
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
        var count by rememberSaveable { mutableStateOf(settingsViewModel.getColumnCount().count.toFloat()) }
        DarkText(text = "Set column count: ${count.toInt()} columns")
        Slider(
            value = count,
            onValueChange = { count = it },
            onValueChangeFinished = { settingsViewModel.setColumnCount(ColumnCount.fromId(count.toInt() - 1)) },
            steps = 2,
            valueRange = 1f..4f,
        )
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
    fun TextSwitch(
        text: String = "",
        checked: Boolean = true,
        onChange: ((Boolean) -> Unit)
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

    private fun getTextColor(darkTheme: Boolean) = if (darkTheme) Color.White else Color.Black
}