package com.example.redditwalls.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.redditwalls.adapters.FavoritesAdapter
import com.example.redditwalls.adapters.ImageClickListener
import com.example.redditwalls.databinding.FragmentFavoritesBinding
import com.example.redditwalls.models.Image
import com.example.redditwalls.viewmodels.FavoritesViewModel
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(), ImageClickListener {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private val favoritesAdapter: FavoritesAdapter by lazy {
        FavoritesAdapter(
            settingsViewModel.loadLowResPreviews(),
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageScroll.apply {
            adapter = favoritesAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        favoritesViewModel.getFavorites().observe(viewLifecycleOwner) {
            favoritesAdapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(image: Image) {
        val toWall =
            FavoritesFragmentDirections.actionNavigationFavoritesToNavigationWallpaper(image)
        findNavController().navigate(toWall)
    }

    override fun onLongClick(image: Image) {
        TODO("Not yet implemented")
    }
}