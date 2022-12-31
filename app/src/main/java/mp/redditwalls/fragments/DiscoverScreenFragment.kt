package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.DiscoverScreen
import mp.redditwalls.viewmodels.DiscoverScreenViewModel

@AndroidEntryPoint
class DiscoverScreenFragment : Fragment() {
    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    private val vm: DiscoverScreenViewModel by viewModels()

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
                    wallpaperHelper = wallpaperHelper,
                    vm = vm
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.updateLikeState()
    }
}