package com.example.redditwalls.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.redditwalls.databinding.FragmentSearchSubsBinding
import com.example.redditwalls.datasources.RWApi
import com.example.redditwalls.models.Resource.Status
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.viewmodels.BottomNavDestinations
import com.example.redditwalls.viewmodels.MainViewModel
import com.example.redditwalls.viewmodels.SearchSubViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview


@AndroidEntryPoint
class SearchSubsFragment : BaseSubsFragment() {

    private val searchSubsViewModel: SearchSubViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentSearchSubsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSubsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(binding.subScroll)
        observeSearchResults()

        binding.searchBar.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                // hide kb
                binding.searchBar.clearFocus()
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!showBanner(s.toString())) {
                    searchSubsViewModel.setQuery(s.toString())
                }
            }
        })

        binding.linkBanner.yes.setOnClickListener {
            val (subreddit, id) = RWApi.extractPostLinkInfo(binding.searchBar.text.toString())
            val toWall =
                SearchSubsFragmentDirections.actionNavigationSearchToNavigationWallpaper(
                    null,
                    id,
                    subreddit
                )
            findNavController().navigate(toWall)
        }

        binding.linkBanner.no.setOnClickListener {
            binding.linkBanner.linkBanner.isVisible = false
        }

        mainViewModel.navIconClicked.observe(viewLifecycleOwner) {
            it?.takeIf { it.first == BottomNavDestinations.SEARCH }?.let {
                binding.searchBar.requestFocus()
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(binding.searchBar, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    fun showBanner(query: String): Boolean {
        val (subreddit, id) = RWApi.extractPostLinkInfo(query)
        return (subreddit.isNotEmpty() && id.isNotEmpty()).also {
            binding.linkBanner.linkBanner.isVisible = it
        }
    }

    @FlowPreview
    private fun observeSearchResults() {
        searchSubsViewModel.queriedResults.observe(viewLifecycleOwner) {
            binding.loading.isVisible = false
            binding.subScroll.isVisible = false
            binding.error.error.isVisible = false
            binding.empty.empty.isVisible = false
            binding.linkBanner.linkBanner.isVisible = false

            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.empty.empty.isVisible = true
                    } else {
                        binding.subScroll.isVisible = true
                        subsAdapter.submitList(it.data)
                    }
                }
                Status.LOADING -> binding.loading.isVisible = true
                Status.ERROR -> {
                    binding.error.error.isVisible = true
                    binding.error.errorLabel.text = it.errorMessage
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(subreddit: Subreddit) {
        val toImages =
            SearchSubsFragmentDirections.actionNavigationSearchToSearchImagesFragment(subreddit.name)
        findNavController().navigate(toImages)
    }
}