package mp.redditwalls.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mp.redditwalls.design.R
import mp.redditwalls.design.RwTheme

@Composable
fun RadioDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    selection: Int,
    options: List<String>,
    confirmButtonText: String,
    cancelButtonText: String,
    onSelectionChanged: (Int) -> Unit,
    onConfirmButtonClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = cancelButtonText)
                }
            },
            title = {
                Text(text = title)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEachIndexed { index, it ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable(onClick = { onSelectionChanged(index) }),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                modifier = Modifier.padding(end = 4.dp),
                                selected = index == selection,
                                onClick = { onSelectionChanged(index) }
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun WallpaperLocationRadioDialog(
    show: Boolean,
    onSubmit: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selection by remember { mutableStateOf(0) }
    RadioDialog(
        show = show,
        title = "Choose Location",
        selection = selection,
        options = listOf(
            stringResource(R.string.home_screen),
            stringResource(R.string.lock_screen),
            stringResource(R.string.both_screens)
        ),
        confirmButtonText = stringResource(R.string.ok),
        cancelButtonText = stringResource(R.string.cancel),
        onSelectionChanged = { selection = it },
        onConfirmButtonClick = {
            onSubmit(selection)
            onDismiss()
        },
        onDismiss = onDismiss
    )
}


@Preview
@Composable
fun RadioDialogPreview() {
    var selection by remember { mutableStateOf(0) }
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RadioDialog(
                show = true,
                title = "Dialog Title",
                options = listOf(
                    "first",
                    "second",
                    "third",
                    "fourth",
                    "fifth"
                ),
                selection = selection,
                confirmButtonText = "ok",
                cancelButtonText = "cancel",
                onSelectionChanged = { selection = it },
                onConfirmButtonClick = {},
                onDismiss = {}
            )
        }
    }
}