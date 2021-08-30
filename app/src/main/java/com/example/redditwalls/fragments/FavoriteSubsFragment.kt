package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.redditwalls.databinding.FragmentFavoriteSubsBinding
import com.example.redditwalls.models.Subreddit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteSubsFragment : BaseSubsFragment() {

    private var _binding: FragmentFavoriteSubsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteSubsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView(binding.subScroll)
        observeFavorites()
    }

    private fun observeFavorites() {
        favoriteSubsViewModel.getFavoriteSubs().observe(viewLifecycleOwner) {
            binding.loading.isVisible = false
            binding.subScroll.isVisible = false
            binding.empty.empty.isVisible = false
            if (it.isNullOrEmpty()) {
                binding.empty.empty.isVisible = true
            } else {
                binding.subScroll.isVisible = true
                subsAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(subreddit: Subreddit) {
        val toImages =
            FavoriteSubsFragmentDirections.actionNavigationSavedToSearchImagesFragment(subreddit)
        findNavController().navigate(toImages)
    }
}