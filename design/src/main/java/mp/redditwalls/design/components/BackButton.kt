package mp.redditwalls.design.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.outline,
    cross: Boolean = false,
    onClick: () -> Unit
) {
    IconButton(modifier = modifier, onClick = onClick) {
        Icon(
            imageVector = if (cross) Icons.Default.Close else Icons.Default.ArrowBack,
            contentDescription = null,
            tint = tint
        )
    }
}
