package com.service.mediataggingapp.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


class InitApplication(context: Context) {

    private var isNightModeEnabled = false

    private var mPrefs: SharedPreferences = context.getSharedPreferences(INIT_MODE, MODE_PRIVATE)

    companion object {
        private const val INIT_MODE = "INIT_MODE"
        private const val NIGHT_MODE = "NIGHT_MODE"
    }

    init {
        this.isNightModeEnabled = mPrefs.getBoolean(NIGHT_MODE, false)
    }


    fun isNightModeEnabled(): Boolean {
        return mPrefs.getBoolean(NIGHT_MODE, false)
    }

    fun setIsNightModeEnabled(isNightModeEnabled: Boolean) {
        this.isNightModeEnabled = isNightModeEnabled
        val editor = mPrefs.edit()
        editor.putBoolean("NIGHT_MODE", isNightModeEnabled)
        editor.apply()
    }

}