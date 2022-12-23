package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.DiscoverScreen

@AndroidEntryPoint
class DiscoverScreenFragment : Fragment() {
    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            RwTheme {
                DiscoverScreen(
                    navController = findNavController(),
                    wallpaperHelper = wallpaperHelper
                )
            }
        }
    }
}