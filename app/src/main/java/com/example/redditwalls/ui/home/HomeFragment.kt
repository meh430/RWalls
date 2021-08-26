package com.example.redditwalls.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.redditwalls.R
import com.example.redditwalls.databinding.FragmentHomeBinding
import com.example.redditwalls.fragments.BaseImagesFragment
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView(binding.imageScroll)
        observeImages()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}