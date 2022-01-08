package com.example.redditwalls.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import com.example.redditwalls.misc.RadioDialog
import com.example.redditwalls.misc.fromId
import com.example.redditwalls.repositories.SettingsItem
import com.example.redditwalls.repositories.Theme
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsComposeFragment : Fragment() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            val sidesPadding = 14.dp
            setContent {
                MaterialTheme {
                    Column(modifier = Modifier.padding(horizontal = sidesPadding)) {
                        OptionSelector(
                            "Select Theme",
                            settingsViewModel.getTheme(),
                            Theme.values()
                        ) {
                            settingsViewModel.setTheme(it)
                            AppCompatDelegate.setDefaultNightMode(it.mode)
                        }
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
            DarkText(dialogTitle, style = MaterialTheme.typography.body1)
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

    private fun getTextColor(darkTheme: Boolean) = if (darkTheme) Color.White else Color.Black
}