package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.HomeScreen

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            RwTheme {
                HomeScreen(wallpaperHelper = wallpaperHelper)
            }
        }
    }
}