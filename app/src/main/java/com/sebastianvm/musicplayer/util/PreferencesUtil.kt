package com.sebastianvm.musicplayer.util

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey


object PreferencesUtil {
    val KEY_NOW_PLAYING_INDEX = intPreferencesKey("KEY_NOW_PLAYING_INDEX")
    val KEY_LAST_RECORDED_POSITION = longPreferencesKey("KEY_LAST_RECORDED_POSITION")

}



