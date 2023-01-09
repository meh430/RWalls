package mp.redditwalls.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun CircleCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val color = MaterialTheme.colorScheme
    val tint = if (checked) color.primary else Color.White

    Box {
        Icon(
            modifier = modifier.clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = { onCheckedChange(!checked) }
            ),
            imageVector = if (checked) Icons.Filled.Circle else Icons.Outlined.Circle,
            tint = Color.White,
            contentDescription = null
        )
        if (checked) {
            Icon(
                modifier = modifier.clickable(
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { onCheckedChange(false) }
                ),
                imageVector = Icons.Filled.CheckCircle,
                tint = tint,
                contentDescription = null
            )
        }
    }
}