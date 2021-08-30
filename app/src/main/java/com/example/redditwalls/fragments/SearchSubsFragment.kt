package com.example.redditwalls.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.redditwalls.adapters.SubredditsAdapter
import com.example.redditwalls.databinding.FragmentSearchSubsBinding
import com.example.redditwalls.models.Resource.Status
import com.example.redditwalls.models.Subreddit
import com.example.redditwalls.viewmodels.SearchSubViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchSubsFragment : BaseSubsFragment() {

    private val searchSubsViewModel: SearchSubViewModel by viewModels()

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
                // Search with new query
                searchSubsViewModel.searchSubs(binding.searchBar.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun observeSearchResults() {
        searchSubsViewModel.searchResults.observe(viewLifecycleOwner) {
            binding.loading.isVisible = false
            binding.subScroll.isVisible = false
            binding.error.error.isVisible = false

            when (it.status) {
                Status.SUCCESS -> {
                    binding.subScroll.isVisible = true

                    if (it.data.isNullOrEmpty()) {
                        binding.empty.empty.isVisible = true
                    } else {
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
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(
        subreddit: Subreddit,
        option: SubredditsAdapter.SubredditMenuOptions
    ) {
        super.onMenuItemClick(subreddit, option)
        searchSubsViewModel.refresh()
    }
}