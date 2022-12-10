package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.FavoriteImagesScreen
import mp.redditwalls.utils.DownloadUtils

@AndroidEntryPoint
class FavoriteImagesScreenFragment : Fragment() {
    @Inject
    lateinit var downloadUtils: DownloadUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            RwTheme {
                FavoriteImagesScreen(
                    downloadUtils = downloadUtils
                )
            }
        }
    }
}