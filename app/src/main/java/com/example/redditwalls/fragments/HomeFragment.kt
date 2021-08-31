package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.redditwalls.databinding.FragmentHomeBinding
import com.example.redditwalls.models.Image
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseApiImagesFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override val subreddit: String
        get() = settingsViewModel.getDefaultSub()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(binding.imageScroll)
        observeImages()
        binding.swipeRefresh.setOnRefreshListener {
            imagesAdapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
        addLoadStateListener(
            binding.imageScroll,
            binding.loading,
            binding.error,
            binding.empty
        )
    }

    override fun onClick(image: Image) {
        val toWall = HomeFragmentDirections.actionNavigationHomeToNavigationWallpaper(image)
        findNavController().navigate(toWall)
    }

    override fun onResume() {
        super.onResume()
        if (imagesViewModel.subredditHasChanged(subreddit)) {
            imagesViewModel.setSubreddit(subreddit)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}