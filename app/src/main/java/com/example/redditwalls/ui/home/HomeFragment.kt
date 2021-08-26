package com.example.redditwalls.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.redditwalls.R
import com.example.redditwalls.databinding.FragmentHomeBinding
import com.example.redditwalls.fragments.BaseImagesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseImagesFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override val toolBarTitle: String
        get() = getString(R.string.title_home)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}