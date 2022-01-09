package com.example.redditwalls.misc

import android.content.Context
import android.widget.Toast
import com.example.redditwalls.repositories.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Toaster @Inject constructor(
    @ApplicationContext val context: Context,
    private val settingsRepository: SettingsRepository
) {
    fun t(message: String) {
        if (settingsRepository.toastEnabled()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}