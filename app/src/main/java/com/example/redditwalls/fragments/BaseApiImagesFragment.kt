package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.redditwalls.R
import com.example.redditwalls.adapters.ImagesAdapter
import com.example.redditwalls.adapters.LoadingStateAdapter
import com.example.redditwalls.databinding.EmptyBinding
import com.example.redditwalls.databinding.ErrorBinding
import com.example.redditwalls.datasources.RWApi.Sort
import com.example.redditwalls.models.Image
import com.example.redditwalls.viewmodels.SettingsViewModel
import com.example.redditwalls.viewmodels.SubImagesViewModel
import com.google.android.material.progressindicator.LinearProgressIndicator

abstract class BaseApiImagesFragment : BaseImagesFragment() {

    abstract val toolBarTitle: String
    abstract val subreddit: String

    protected val imagesAdapter: ImagesAdapter by lazy {
        val loadLowRes = settingsViewModel.loadLowResPreviews()
        ImagesAdapter(loadLowRes, this).apply {
            withLoadStateFooter(
                footer = LoadingStateAdapter(this)
            )
        }
    }
    protected val settingsViewModel: SettingsViewModel by viewModels()
    private val imagesViewModel: SubImagesViewModel by lazy {
        val vm: SubImagesViewModel by viewModels()
        vm.also {
            it.initialize(subreddit)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    protected fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            adapter = imagesAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_hot, R.id.sort_new, R.id.sort_top_week,
            R.id.sort_top_month, R.id.sort_top_year, R.id.sort_top_all -> {
                val sort = Sort.fromId(item.itemId)
                if (imagesViewModel.currentSort != sort) {
                    setSort(sort)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setQuery(query: String) {
        imagesViewModel.setQuery(query)
    }

    fun setSort(sort: Sort = Sort.HOT) {
        imagesViewModel.setSort(sort)
    }

    fun observeImages() {
        imagesViewModel.imagePages.observe(viewLifecycleOwner, {
            imagesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    fun addLoadStateListener(
        recyclerView: RecyclerView,
        progressBar: LinearProgressIndicator,
        errorState: ErrorBinding,
        emptyView: EmptyBinding
    ) {
        imagesAdapter.addLoadStateListener {
            val isEmpty = it.source.refresh is LoadState.NotLoading &&
                    it.append.endOfPaginationReached && imagesAdapter.itemCount < 1
            val isLoading = it.refresh is LoadState.Loading || it.append is LoadState.Loading
            val hasError = it.refresh is LoadState.Error

            recyclerView.isVisible = false
            errorState.error.isVisible = false
            progressBar.hide()
            emptyView.empty.isVisible = false

            errorState.error.isVisible = hasError
            errorState.errorLabel.text = (it.refresh as? LoadState.Error)?.error?.message
            if (isLoading) {
                progressBar.show()
            } else {
                progressBar.hide()
            }
            emptyView.empty.isVisible = isEmpty
            recyclerView.isVisible = !isEmpty && !hasError
        }
    }

    override fun onClick(image: Image) {
        val toWall = HomeFragmentDirections.actionNavigationHomeToNavigationWallpaper(image)
        findNavController().navigate(toWall)
    }
}