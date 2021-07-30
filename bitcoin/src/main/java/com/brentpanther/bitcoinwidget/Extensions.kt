package com.brentpanther.bitcoinwidget

import androidx.preference.ListPreference
import androidx.preference.TwoStatePreference

fun TwoStatePreference.onUpdate(func: (value: Boolean) -> kotlin.Unit) = setOnPreferenceChangeListener { _, newValue ->
    isChecked = newValue as Boolean
    func(isChecked)
    true
}

fun ListPreference.onUpdate(func: (value: String) -> kotlin.Unit) = setOnPreferenceChangeListener { _, newValue ->
    value = newValue as String?
    func(value)
    true
}