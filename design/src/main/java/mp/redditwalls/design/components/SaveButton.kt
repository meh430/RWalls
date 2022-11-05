package mp.redditwalls.design.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    isSaved: Boolean,
    onClick: (Boolean) -> Unit
) {
    IconButton(modifier = modifier, onClick = { onClick(!isSaved) }) {
        Icon(
            modifier = Modifier.size(size),
            imageVector = if (isSaved) {
                Icons.Default.Bookmark
            } else {
                Icons.Default.BookmarkBorder
            },
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
    }
}