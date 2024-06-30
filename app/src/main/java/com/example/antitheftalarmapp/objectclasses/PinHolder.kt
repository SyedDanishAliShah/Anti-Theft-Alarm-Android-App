package com.example.antitheftalarmapp.objectclasses

import android.content.Context
import android.content.SharedPreferences

class PinHolder private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var enteredPin: String get() = sharedPreferences.getString(KEY_ENTERED_PIN, "") ?: ""
        set(value) { sharedPreferences.edit().putString(KEY_ENTERED_PIN, value).apply() }

    companion object {
        private const val PREFS_NAME = "PinHolderPrefs"
        private const val KEY_ENTERED_PIN = "enteredPin"

        @Volatile
        private var INSTANCE: PinHolder? = null

        fun getInstance(context: Context): PinHolder {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PinHolder(context).also { INSTANCE = it }
            }
        }
    }
}