package com.example.redditwalls.fragments

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.redditwalls.WallpaperHelper
import com.example.redditwalls.adapters.ImageClickListener
import com.example.redditwalls.models.Image
import com.example.redditwalls.viewmodels.FavoritesViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseImagesFragment : Fragment(), ImageClickListener {
    protected val favoritesViewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    override fun onDoubleClick(image: Image) {
        lifecycleScope.launch {
            val added = favoritesViewModel.addFavorite(image)
            val msg = if (added) "Added to favorites" else "Removed from favorites"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLongClick(image: Image) {
        wallpaperHelper.showLocationPickerDialog(requireContext()) {
            lifecycleScope.launch {
                wallpaperHelper.setImageLinkAsWallpaper(requireContext(), image.imageLink, it)
            }
        }
    }
}