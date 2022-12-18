package mp.redditwalls.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.outline,
    cross: Boolean = false,
    onClick: () -> Unit
) {
    Icon(
        imageVector = if (cross) Icons.Default.Close else Icons.Default.ArrowBack,
        contentDescription = null,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        tint = tint
    )
}
