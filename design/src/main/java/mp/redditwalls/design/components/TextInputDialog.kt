package mp.redditwalls.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import mp.redditwalls.design.R
import mp.redditwalls.design.RwTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    label: String,
    validPattern: Regex? = null,
    errorText: String = "",
    confirmButtonText: String = stringResource(R.string.ok),
    cancelButtonText: String = stringResource(R.string.cancel),
    onConfirmButtonClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        var text by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        if (validPattern == null || validPattern.matches(text)) {
                            onConfirmButtonClick(text)
                            onDismiss()
                        } else {
                            isError = true
                        }
                    }
                ) {
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
                    OutlinedTextField(
                        value = text,
                        isError = isError,
                        label = {
                            Text(
                                if (isError) {
                                    errorText
                                } else {
                                    label
                                }
                            )
                        },
                        onValueChange = {
                            text = it
                            isError = false
                        },
                        singleLine = true,
                        maxLines = 1
                    )
                }
            }
        )
    }
}

private val folderNamePattern by lazy {
    Regex("""^[a-zA-z0-9]{1,20}${'$'}""")
}

@Composable
fun AddFolderDialog(
    show: Boolean,
    onConfirmButtonClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    TextInputDialog(
        show = show,
        title = "Create a folder",
        label = "Folder name",
        validPattern = folderNamePattern,
        errorText = "Must be <20 char. and alphanumeric",
        onConfirmButtonClick = onConfirmButtonClick,
        onDismiss = onDismiss
    )
}

@Preview
@Composable
fun TextInputDialogPreview() {
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddFolderDialog(
                show = true,
                onConfirmButtonClick = {},
                onDismiss = {}
            )
        }
    }
}