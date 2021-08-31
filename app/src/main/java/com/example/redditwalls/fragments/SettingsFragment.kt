package com.example.redditwalls.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.*
import com.example.redditwalls.R
import com.example.redditwalls.WallpaperHelper
import com.example.redditwalls.WallpaperLocation
import com.example.redditwalls.databinding.FragmentSettingsBinding
import com.example.redditwalls.misc.RandomRefreshWorker
import com.example.redditwalls.repositories.SettingsRepository.Companion.FALLBACK_SUBREDDIT
import com.example.redditwalls.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        const val RANDOM_REFRESH_WORK = "random_refresh_work"
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

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
        addListeners()
    }

    override fun onResume() {
        super.onResume()
        populateSettings()
    }

    private fun populateSettings() {
        setLoading(true)

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

        setLoading(false)
    }

    private fun addListeners() {
        binding.defaultSubreddit.editText?.setOnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                binding.defaultSubreddit.clearFocus()
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(binding.defaultSubreddit.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.randomRefreshSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.randomRefreshSettings.isVisible = isChecked
        }

        binding.locationButton.setOnClickListener {
            wallpaperHelper.showLocationPickerDialog(requireContext()) {
                binding.locationButton.text = it.displayText
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveSettings()
    }

    private fun saveSettings() {
        settingsViewModel.setDefaultSub(binding.defaultSubreddit.editText?.text.toString())
        settingsViewModel.setLoadLowResPreviews(binding.lowResPreviewsSwitch.isChecked)

        val randomRefreshEnabled = binding.randomRefreshSwitch.isChecked
        settingsViewModel.setRandomRefresh(randomRefreshEnabled)
        if (binding.randomRefreshSwitch.isChecked) {
            val interval = when (binding.intervalGroup.checkedRadioButtonId) {
                R.id.one -> 1
                R.id.six -> 6
                R.id.twelve -> 12
                R.id.twentyFour -> 24
                else -> 1
            }
            val location = WallpaperLocation.values().find {
                it.displayText == binding.locationButton.text.toString()
            } ?: WallpaperLocation.BOTH

            // Only add new worker if settings changed
            if (settingsViewModel.randomRefreshSettingsChanged(interval, location)) {
                settingsViewModel.setRandomRefreshPeriod(interval)
                settingsViewModel.setRandomRefreshLocation(location)
                startRandomRefreshWork(interval)
            }

        } else {
            cancelRandomRefreshWork()
        }
    }

    private fun startRandomRefreshWork(interval: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<RandomRefreshWorker>(
            interval.toLong(),
            TimeUnit.HOURS
        ).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.enqueueUniquePeriodicWork(
            RANDOM_REFRESH_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            work
        )
    }

    private fun cancelRandomRefreshWork() {
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.cancelUniqueWork(RANDOM_REFRESH_WORK)
    }

    private fun setLoading(isLoading: Boolean) {
        val hasLoaded = !isLoading

        binding.apply {
            defaultSubreddit.isVisible = hasLoaded
            lowResPreviewsSwitch.isVisible = hasLoaded
            randomRefreshSwitch.isVisible = hasLoaded
            randomRefreshSettings.isVisible = hasLoaded

            loading.isVisible = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}