package mp.redditwalls.design.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun OptionsMenu(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.MoreVert,
    options: List<IconText>,
    onOptionSelected: (Int) -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    Box(modifier) {
        IconButton(
            onClick = { sortMenuExpanded = true }
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
        PopupMenu(
            expanded = sortMenuExpanded,
            options = options,
            onOptionSelected = onOptionSelected,
            onDismiss = { sortMenuExpanded = false }
        )
    }
}
