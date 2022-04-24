package com.sebastianvm.musicplayer.repository.playback

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.MediaQueueItem
import com.sebastianvm.musicplayer.player.PlaybackInfo
import com.sebastianvm.musicplayer.util.PreferencesUtil
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaybackInfoDataSourceImpl @Inject constructor(
    private val mediaQueueDao: MediaQueueDao,
    private val playbackInfoDataStore: DataStore<Preferences>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : PlaybackInfoDataSource {
    override suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo) {
        playbackInfoDataStore.edit { savedPrefs ->
            savedPrefs[PreferencesUtil.KEY_LAST_RECORDED_POSITION] =
                newPlaybackInfo.lastRecordedPosition
            savedPrefs[PreferencesUtil.KEY_NOW_PLAYING_INDEX] = newPlaybackInfo.nowPlayingIndex
        }
        withContext(ioDispatcher) {
            mediaQueueDao.saveQueue(newPlaybackInfo.queuedTracks.mapIndexed { index, track ->
                MediaQueueItem(
                    track.trackId,
                    index
                )
            })
        }
    }

    override fun getSavedPlaybackInfo(): Flow<PlaybackInfo> {
        return combine(
            playbackInfoDataStore.data,
            mediaQueueDao.getQueuedTracks()
        ) { prefs, tracks ->
            PlaybackInfo(
                queuedTracks = tracks,
                nowPlayingIndex = prefs[PreferencesUtil.KEY_NOW_PLAYING_INDEX] ?: -1,
                lastRecordedPosition = prefs[PreferencesUtil.KEY_LAST_RECORDED_POSITION] ?: 0
            )
        }
    }
}