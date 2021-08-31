package com.example.redditwalls.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.redditwalls.MainActivity
import com.example.redditwalls.databinding.FragmentSearchImagesBinding
import com.example.redditwalls.models.Image
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchImagesFragment : BaseApiImagesFragment() {

    private val navArgs: SearchImagesFragmentArgs by navArgs()

    private var _binding: FragmentSearchImagesBinding? = null
    private val binding get() = _binding!!

    override val subreddit: String
        get() = navArgs.subreddit.name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setToolbarTitle(subreddit)

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

        binding.searchBar.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                // hide kb
                binding.searchBar.clearFocus()
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
                val query = binding.searchBar.text.toString()

                if (query.isEmpty()) {
                    // Search with new query
                    imagesViewModel.setQuery("")
                } else {
                    imagesViewModel.setQuery(query)
                }
                true
            } else {
                false
            }
        }
    }

    override fun onClick(image: Image) {
        val toWall =
            SearchImagesFragmentDirections.actionSearchImagesFragmentToNavigationWallpaper(image)
        findNavController().navigate(toWall)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}