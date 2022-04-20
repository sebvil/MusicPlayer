package com.sebastianvm.musicplayer.util

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object PreferencesUtil {
    val SAVED_PLAYBACK_INFO_MEDIA_GROUP =
        stringPreferencesKey("SAVED_PLAYBACK_INFO_MEDIA_GROUP")
    val SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID =
        stringPreferencesKey("SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID")
    val SAVED_PLAYBACK_INFO_MEDIA_ID = stringPreferencesKey("CURRENT_PLAYBACK_MEDIA_ID")
    val SAVED_PLAYBACK_INFO_POSITION = longPreferencesKey("SAVED_PLAYBACK_INFO_POSITION")

}



