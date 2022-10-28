package mp.redditwalls.design.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import mp.redditwalls.design.RwTheme

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    title: String,
    body: String,
    confirmButtonText: String,
    cancelButtonText: String,
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
                Text(text = body)
            }
        )
    }
}

@Preview
@Composable
fun ConfirmationDialogPreview() {
    RwTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ConfirmationDialog(
                show = true,
                title = "Dialog Title",
                body = "This area typically contains the supportive text which presents the details regarding the Dialog's purpose.",
                confirmButtonText = "ok",
                cancelButtonText = "cancel",
                onConfirmButtonClick = {},
                onDismiss = {}
            )
        }
    }
}