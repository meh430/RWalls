package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.redditwalls.R
import com.example.redditwalls.adapters.ImagesAdapter
import com.example.redditwalls.datasources.RWApi.Sort
import com.example.redditwalls.viewmodels.SettingsViewModel
import com.example.redditwalls.viewmodels.SubImagesViewModel

abstract class BaseImagesFragment : Fragment() {

    abstract val toolBarTitle: String
    abstract val subreddit: String

    private val imagesAdapter: ImagesAdapter by lazy {
        val loadLowRes = settingsViewModel.loadLowResPreviews()
        ImagesAdapter(loadLowRes)
    }
    protected val settingsViewModel: SettingsViewModel by viewModels()
    protected val imagesViewModel: SubImagesViewModel by lazy {
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
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setSort(Sort.fromId(item.itemId))
        return true
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
}