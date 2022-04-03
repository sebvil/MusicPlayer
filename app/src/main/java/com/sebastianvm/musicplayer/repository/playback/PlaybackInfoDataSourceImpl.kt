package com.sebastianvm.musicplayer.repository.playback

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.util.PreferencesUtil
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaybackInfoDataSourceImpl @Inject constructor(private val playbackInfoDataStore: DataStore<Preferences>, @IODispatcher private val ioDispatcher: CoroutineDispatcher) :
    PlaybackInfoDataSource {
    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) {
        withContext(ioDispatcher) {
            with(transform(getSavedPlaybackInfo().first())) {
                playbackInfoDataStore.edit { settings ->
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP] =
                        currentQueue.mediaGroupType.name
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID] =
                        currentQueue.mediaId
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_ID] = mediaId
                    settings[PreferencesUtil.SAVED_PLAYBACK_INFO_POSITION] = lastRecordedPosition
                }
            }
        }
    }

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        return playbackInfoDataStore.data.map { preferences ->
            val mediaGroup =
                preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP]
                    ?: MediaGroupType.UNKNOWN.name
            val mediaGroupId = preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_GROUP_ID] ?: ""
            val mediaId = preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_MEDIA_ID] ?: ""
            val position = preferences[PreferencesUtil.SAVED_PLAYBACK_INFO_POSITION] ?: 0
            SavedPlaybackInfo(
                MediaGroup(MediaGroupType.valueOf(mediaGroup), mediaGroupId),
                mediaId,
                position
            )
        }
    }
}