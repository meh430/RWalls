package mp.redditwalls.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.parcelize.Parcelize
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.RwTheme
import mp.redditwalls.domain.models.ImageId
import mp.redditwalls.ui.screens.WallpaperScreen
import mp.redditwalls.utils.DownloadUtils
import mp.redditwalls.utils.Utils
import mp.redditwalls.utils.onWriteStoragePermissionGranted
import mp.redditwalls.utils.parcelable

@AndroidEntryPoint
class WallpaperScreenActivity : ComponentActivity() {
    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    @Inject
    lateinit var downloadUtils: DownloadUtils

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            onWriteStoragePermissionGranted(it)
            findViewById<View>(android.R.id.content)?.let { v ->
                Utils.setFullScreen(window, v)
            }
        }

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
                    wallpaperHelper = wallpaperHelper,
                    downloadUtils = downloadUtils,
                    arguments = arguments,
                    onWritePermissionRequest = {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
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
    val imageId: ImageId
) : Parcelable
