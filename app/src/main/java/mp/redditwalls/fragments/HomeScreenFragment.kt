package mp.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import mp.redditwalls.composables.screens.HomeScreen
import mp.redditwalls.design.RwTheme

@AndroidEntryPoint
class HomeScreenFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            RwTheme {
                HomeScreen()
            }
        }
    }
}