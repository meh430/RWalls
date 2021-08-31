package com.example.redditwalls.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.redditwalls.R
import com.example.redditwalls.databinding.FragmentSettingsBinding
import com.example.redditwalls.repositories.SettingsRepository.Companion.FALLBACK_SUBREDDIT
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateSettings()
    }

    override fun onResume() {
        super.onResume()
        populateSettings()
    }

    private fun populateSettings() {
        val defaultSubreddit = settingsViewModel.getDefaultSub().takeIf {
            it.isNotEmpty()
        } ?: FALLBACK_SUBREDDIT
        binding.defaultSubreddit.editText?.setText(defaultSubreddit)

        binding.lowResPreviewsSwitch.isChecked = settingsViewModel.loadLowResPreviews()

        val refreshOn = settingsViewModel.randomRefreshEnabled()
        binding.randomRefreshSwitch.isChecked = refreshOn
        binding.randomRefreshSettings.isVisible = refreshOn
        if (refreshOn) {
            val checkedInterval = when (settingsViewModel.getRandomRefreshPeriod()) {
                1 -> R.id.one
                6 -> R.id.six
                12 -> R.id.twelve
                24 -> R.id.twentyFour
                else -> R.id.one
            }
            binding.intervalGroup.check(checkedInterval)

            val setLocation = settingsViewModel.getRandomRefreshLocation()
            binding.locationButton.text = setLocation.displayText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}