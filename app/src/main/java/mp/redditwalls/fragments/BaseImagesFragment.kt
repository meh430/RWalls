package mp.redditwalls.fragments

import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject
import kotlinx.coroutines.launch
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.adapters.ImagePageListener
import mp.redditwalls.utils.Toaster
import mp.redditwalls.models.Image
import mp.redditwalls.repositories.ColumnCount
import mp.redditwalls.viewmodels.FavoritesViewModel
import mp.redditwalls.viewmodels.SettingsViewModel


abstract class BaseImagesFragment : Fragment(), ImagePageListener {
    protected val favoritesViewModel: FavoritesViewModel by viewModels()
    protected val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    @Inject
    lateinit var toaster: Toaster

    override fun onDoubleClick(image: Image) {
        lifecycleScope.launch {
            val added = favoritesViewModel.addFavorite(image)
            val msg = if (added) "Added to favorites" else "Removed from favorites"

            toaster.t(msg, force = true)
        }
    }

    override fun onLongClick(image: Image) {
        wallpaperHelper.showLocationPickerDialog(requireContext()) {
            lifecycleScope.launch {
                wallpaperHelper.setImageAsWallpaper(requireContext(), image.imageLink, it)
            }
        }
    }

    override fun onSetWallpaper(image: Image) {
        onLongClick(image)
    }

    override fun onClickInfo(image: Image) {}

    override fun onLike(image: Image) {}

    fun getLayoutManager() = settingsViewModel.getColumnCount().let {
        if (it == ColumnCount.ONE) {
            LinearLayoutManager(requireContext())
        } else {
            GridLayoutManager(requireContext(), it.count)
        }
    }

    override fun onResume() {
        super.onResume()
        settingsViewModel.getAnimationsEnabled().also {
            if (it != settingsViewModel.animateTransition) {
                settingsViewModel.animateTransition = it
            }
        }
    }

    fun navigateToWall(
        imageView: View?,
        action: NavDirections,
        turnOffAnimations: Boolean = false
    ) {
        if (imageView == null || !settingsViewModel.animateTransition || turnOffAnimations) {
            findNavController().navigate(action)
        } else {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                androidx.core.util.Pair.create(imageView, "redditImage")
            )

            val extras = ActivityNavigatorExtras(options)
            findNavController().navigate(action, extras)
        }
    }
}