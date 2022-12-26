package mp.redditwalls.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LikeButton(
    modifier: Modifier = Modifier,
    unlikedTint: Color = Color.White,
    isLiked: Boolean,
    onLikeClick: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val icon = if (isLiked) {
        Icons.Filled.Favorite
    } else {
        Icons.Filled.FavoriteBorder
    }
    val iconTint = if (isLiked) {
        Color.Red
    } else {
        unlikedTint
    }

    Icon(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = { onLikeClick(!isLiked) }
            ),
        imageVector = icon,
        contentDescription = null,
        tint = iconTint
    )
}