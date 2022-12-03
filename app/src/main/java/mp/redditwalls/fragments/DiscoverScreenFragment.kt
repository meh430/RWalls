package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import mp.redditwalls.design.RwTheme
import mp.redditwalls.ui.screens.DiscoverScreen

@AndroidEntryPoint
class DiscoverScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            RwTheme {
                DiscoverScreen()
            }
        }
    }
}