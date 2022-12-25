package mp.redditwalls.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.WallpaperScreen
import mp.redditwalls.utils.Utils
import mp.redditwalls.utils.parcelable

@AndroidEntryPoint
class WallpaperScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arguments = intent.parcelable<WallpaperActivityArguments>(
            WALLPAPER_ACTIVITY_ARGUMENTS
        ) ?: return

        findViewById<View>(android.R.id.content)?.let {
            Utils.setFullScreen(window, it)
        }

        setContent {
            RwTheme {
                WallpaperScreen(
                    arguments = arguments
                )
            }
        }
    }

    companion object {
        private const val WALLPAPER_ACTIVITY_ARGUMENTS = "WALLPAPER_ACTIVITY_ARGUMENTS"

        fun launch(
            context: Context,
            arguments: WallpaperActivityArguments
        ) {
            context.startActivity(
                Intent(context, WallpaperScreenActivity::class.java).apply {
                    putExtra(WALLPAPER_ACTIVITY_ARGUMENTS, arguments)
                }
            )
        }
    }
}

@Parcelize
data class WallpaperActivityArguments(
    val imageNetworkId: String
) : Parcelable
