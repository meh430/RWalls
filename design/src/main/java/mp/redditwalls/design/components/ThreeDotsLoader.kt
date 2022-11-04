package mp.redditwalls.design.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun ThreeDotsLoader(modifier: Modifier = Modifier) {
    val dots = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) },
    )

    dots.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f, animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 2000
                        0.0f at 0 with LinearOutSlowInEasing // for 0-15 ms
                        1.0f at 200 with LinearOutSlowInEasing // for 15-75 ms
                        0.0f at 400 with LinearOutSlowInEasing // for 0-15 ms
                        0.0f at 2000
                    },
                    repeatMode = RepeatMode.Restart,
                )
            )
        }
    }

    val dys = dots.map { it.value }

    val travelDistance = with(LocalDensity.current) { 15.dp.toPx() }

    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        dys.forEachIndexed { index, dy ->
            Box(
                Modifier
                    .size(25.dp)
                    .graphicsLayer {
                        translationY = -dy * travelDistance
                    },
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                )
            }

            if (index != dys.size - 1) {
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}