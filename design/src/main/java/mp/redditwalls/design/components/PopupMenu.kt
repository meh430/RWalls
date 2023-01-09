package mp.redditwalls.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import mp.redditwalls.design.RwTheme

@Composable
fun PopupMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    options: List<IconText>,
    onOptionSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        options.forEachIndexed { index, iconText ->
            val leadingIcon: (@Composable () -> Unit)? = iconText.icon?.let {
                @Composable {
                    Icon(
                        imageVector = it,
                        contentDescription = null
                    )
                }
            }
            DropdownMenuItem(
                text = { Text(iconText.text) },
                onClick = {
                    onOptionSelected(index)
                    onDismiss()
                },
                leadingIcon = leadingIcon
            )
        }
    }
}

@Preview
@Composable
fun PopupMenuPreview() {
    var expanded by remember { mutableStateOf(false) }
    RwTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }
            PopupMenu(
                expanded = expanded,
                options = listOf(
                    IconText("Option 1"),
                    IconText("Option 2"),
                    IconText("Option 3"),
                    IconText("Option 4"),
                ),
                onOptionSelected = {},
                onDismiss = { expanded = false }
            )
        }
    }
}