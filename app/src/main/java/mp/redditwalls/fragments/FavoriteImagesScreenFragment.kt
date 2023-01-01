package mp.redditwalls.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.FavoriteImagesScreen
import mp.redditwalls.utils.DownloadUtils
import mp.redditwalls.utils.onWriteStoragePermissionGranted

@AndroidEntryPoint
class FavoriteImagesScreenFragment : Fragment() {
    @Inject
    lateinit var downloadUtils: DownloadUtils

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            requireContext().onWriteStoragePermissionGranted(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            RwTheme {
                FavoriteImagesScreen(
                    wallpaperHelper = wallpaperHelper,
                    downloadUtils = downloadUtils,
                    onWritePermissionRequest = {
                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                )
            }
        }
    }
}