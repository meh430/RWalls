package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.HomeScreen
import mp.redditwalls.viewmodels.HomeScreenViewModel

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    private val vm: HomeScreenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            RwTheme {
                HomeScreen(
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