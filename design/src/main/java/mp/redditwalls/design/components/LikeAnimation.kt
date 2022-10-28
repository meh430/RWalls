package mp.redditwalls.design.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import mp.redditwalls.design.R

@Composable
fun LikeAnimation(
    modifier: Modifier = Modifier,
    show: Boolean,
    onAnimationComplete: () -> Unit
) {
    if (show) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.like))
        val progress by animateLottieCompositionAsState(composition)
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            progress = {
                if (progress == 1f) {
                    onAnimationComplete()
                }
                progress
            }
        )
    }
}
