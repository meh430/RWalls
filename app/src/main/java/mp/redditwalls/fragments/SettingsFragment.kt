package mp.redditwalls.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import mp.redditwalls.R
import mp.redditwalls.WallpaperHelper
import mp.redditwalls.WallpaperLocation
import mp.redditwalls.databinding.FragmentSettingsBinding
import mp.redditwalls.datasources.RWApi
import mp.redditwalls.utils.RadioDialog
import mp.redditwalls.utils.RandomRefreshWorker
import mp.redditwalls.utils.fromDisplayText
import mp.redditwalls.utils.launchBrowser
import mp.redditwalls.repositories.ColumnCount
import mp.redditwalls.repositories.RefreshInterval
import mp.redditwalls.repositories.SettingsRepository.Companion.FALLBACK_SUBREDDIT
import mp.redditwalls.repositories.Theme
import mp.redditwalls.viewmodels.SettingsViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        const val RANDOM_REFRESH_WORK = "random_refresh_work"
        private const val REPO_LINK = "https://github.com/meh430/RedditWalls-rewrite"
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    @Inject
    lateinit var wallpaperHelper: WallpaperHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        addTips()
    }

    override fun onResume() {
        super.onResume()
        populateSettings()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.favorites_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actions -> REPO_LINK.launchBrowser(requireActivity()).let { true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateSettings() {
        val theme = settingsViewModel.getTheme()
        binding.themeButton.text = theme.displayText

        val sort = settingsViewModel.getDefaultSort()
        binding.sortButton.text = sort.displayText

        val defaultSubreddit = settingsViewModel.getDefaultSub().takeIf {
            it.isNotEmpty()
        } ?: FALLBACK_SUBREDDIT
        binding.defaultSubreddit.editText?.setText(defaultSubreddit)

        binding.columnCountSlider.value = settingsViewModel.getColumnCount().count.toFloat()

        binding.lowResPreviewsSwitch.isChecked = settingsViewModel.loadLowResPreviews()
        binding.animationEnabledSwitch.isChecked = settingsViewModel.getAnimationsEnabled()

        val refreshOn = settingsViewModel.randomRefreshEnabled()
        binding.randomRefreshSwitch.isChecked = refreshOn
        binding.randomRefreshSettings.isVisible = refreshOn

        if (refreshOn) {
            val refreshInterval = settingsViewModel.getRandomRefreshInterval()
            binding.refreshPeriodButton.text = refreshInterval.displayText

            val setLocation = settingsViewModel.getRandomRefreshLocation()
            binding.locationButton.text = setLocation.displayText
        }
    }

    private fun addListeners() {
        binding.themeButton.setOnClickListener {
            val themes = Theme.values()
            RadioDialog(
                requireContext(),
                "Set Theme",
                themes,
                themes.fromDisplayText(binding.themeButton.text.toString(), Theme.SYSTEM).id
            ).show {
                val selectedTheme = Theme.fromId(it)
                binding.themeButton.text = selectedTheme.displayText
                AppCompatDelegate.setDefaultNightMode(selectedTheme.mode)
            }
        }

        binding.sortButton.setOnClickListener {
            val sorts = RWApi.Sort.values()
            RadioDialog(
                requireContext(),
                "Set Default Sort",
                sorts,
                sorts.fromDisplayText(binding.sortButton.text.toString(), RWApi.Sort.HOT).id
            ).show {
                binding.sortButton.text = RWApi.Sort.fromId(it).displayText
            }
        }

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

        binding.refreshPeriodButton.setOnClickListener {
            val intervals = RefreshInterval.values()
            RadioDialog(
                requireContext(),
                "Set Refresh Interval",
                intervals,
                intervals.fromDisplayText(
                    binding.refreshPeriodButton.text.toString(),
                    RefreshInterval.ONE_H
                ).id
            ).show {
                binding.refreshPeriodButton.text = RefreshInterval.fromId(it).displayText
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveSettings()
    }

    private fun saveSettings() {
        settingsViewModel.setTheme(
            Theme.values().fromDisplayText(binding.themeButton.text.toString(), Theme.SYSTEM)
        )
        settingsViewModel.setDefaultSort(
            RWApi.Sort.values().fromDisplayText(binding.sortButton.text.toString(), RWApi.Sort.HOT)
        )

        settingsViewModel.setDefaultSub(
            binding.defaultSubreddit.editText?.text.toString().trim().replace("\\s".toRegex(), "")
        )
        val count = binding.columnCountSlider.value.toInt()
        settingsViewModel.setColumnCount(
            ColumnCount.values().find { it.count == count } ?: ColumnCount.TWO
        )
        settingsViewModel.setLoadLowResPreviews(binding.lowResPreviewsSwitch.isChecked)
        settingsViewModel.setAnimationsEnabled(binding.animationEnabledSwitch.isChecked)

        val randomRefreshEnabled = binding.randomRefreshSwitch.isChecked
        settingsViewModel.setRandomRefresh(randomRefreshEnabled)
        if (binding.randomRefreshSwitch.isChecked) {
            val refreshDisplayText = binding.refreshPeriodButton.text.toString()
            val interval =
                RefreshInterval.values().fromDisplayText(
                    refreshDisplayText,
                    RefreshInterval.ONE_H
                )
            val location = WallpaperLocation.values()
                .fromDisplayText(binding.locationButton.text.toString(), WallpaperLocation.BOTH)

            // Only add new worker if settings changed
            if (settingsViewModel.randomRefreshSettingsChanged()) {
                settingsViewModel.setRandomRefreshInterval(interval)
                settingsViewModel.setRandomRefreshLocation(location)
                startRandomRefreshWork(interval)
            }

        } else {
            cancelRandomRefreshWork()
            settingsViewModel.clearRandomRefreshSettings()
        }
    }

    private fun startRandomRefreshWork(interval: RefreshInterval) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<RandomRefreshWorker>(
            interval.amount,
            interval.timeUnit
        ).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.enqueueUniquePeriodicWork(
            RANDOM_REFRESH_WORK,
            ExistingPeriodicWorkPolicy.REPLACE,
            work
        )
        Toast.makeText(requireContext(), "Start refresh", Toast.LENGTH_SHORT).show()
    }

    private fun cancelRandomRefreshWork() {
        val workManager = WorkManager.getInstance(requireContext().applicationContext)
        workManager.cancelUniqueWork(RANDOM_REFRESH_WORK)
        Toast.makeText(requireContext(), "Cancels refresh", Toast.LENGTH_SHORT).show()
    }

    private fun addTips() {
        val tips = resources.getStringArray(R.array.tips).joinToString(separator = "\n")
        binding.tips.text = tips
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}