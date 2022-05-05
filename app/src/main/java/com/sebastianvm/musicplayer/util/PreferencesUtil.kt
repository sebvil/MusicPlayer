package com.sebastianvm.musicplayer.util

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object PreferencesUtil {
    val KEY_NOW_PLAYING_INDEX = stringPreferencesKey("KEY_NOW_PLAYING_INDEX")
    val KEY_LAST_RECORDED_POSITION = longPreferencesKey("KEY_LAST_RECORDED_POSITION")

}



