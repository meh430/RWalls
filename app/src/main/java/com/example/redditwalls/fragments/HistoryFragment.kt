package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.redditwalls.adapters.HistoryAdapter
import com.example.redditwalls.databinding.FragmentHistoryBinding
import com.example.redditwalls.viewmodels.HistoryViewModel
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val historyAdapter: HistoryAdapter by lazy {
        HistoryAdapter(settingsViewModel.loadLowResPreviews()) {
            val image = it.toImage()
            val action = HistoryFragmentDirections.actionHistoryFragmentToNavigationWallpaper(
                image,
                null,
                null
            )
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.historyScroll.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        observeHistory()
    }

    private fun observeHistory() {
        historyViewModel.getHistory().observe(viewLifecycleOwner) {
            binding.historyScroll.isVisible = false
            binding.empty.empty.isVisible = false
            if (it.isNullOrEmpty()) {
                binding.empty.empty.isVisible = true
            } else {
                binding.historyScroll.isVisible = true
                historyAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}