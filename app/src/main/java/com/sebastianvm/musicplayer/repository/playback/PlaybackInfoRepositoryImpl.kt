package com.sebastianvm.musicplayer.repository.playback

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.MediaQueueItem
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.player.PlaybackInfo
import com.sebastianvm.musicplayer.util.PreferencesUtil
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.extensions.uniqueId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaybackInfoRepositoryImpl @Inject constructor(
    private val mediaQueueDao: MediaQueueDao,
    private val playbackInfoDataStore: DataStore<Preferences>,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : PlaybackInfoRepository {

    override suspend fun modifySavedPlaybackInfo(player: Player) {
        val timeline = player.currentTimeline
        val id = player.currentMediaItem?.uniqueId ?: 0L
        val contentPosition = player.contentPosition
        withContext(ioDispatcher) {
            val newQueue = (0 until timeline.windowCount).map {
                TrackWithQueueId.fromMediaItem(
                    mediaItem = timeline.getWindow(
                        it,
                        Timeline.Window()
                    ).mediaItem
                )
            }
            val newPlaybackInfo = PlaybackInfo(
                queuedTracks = newQueue,
                nowPlayingId = id,
                lastRecordedPosition = contentPosition
            )
            modifySavedPlaybackInfo(newPlaybackInfo)

        }
    }


    private suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo) {
        playbackInfoDataStore.edit { savedPrefs ->
            savedPrefs[PreferencesUtil.KEY_LAST_RECORDED_POSITION] =
                newPlaybackInfo.lastRecordedPosition
            savedPrefs[PreferencesUtil.KEY_NOW_PLAYING_INDEX] = newPlaybackInfo.nowPlayingId
        }
        withContext(ioDispatcher) {
            mediaQueueDao.saveQueue(newPlaybackInfo.queuedTracks.mapIndexed { index, track ->
                MediaQueueItem(
                    track.id,
                    index,
                    track.uniqueQueueItemId
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
                nowPlayingId = prefs[PreferencesUtil.KEY_NOW_PLAYING_INDEX] ?: 0L,
                lastRecordedPosition = prefs[PreferencesUtil.KEY_LAST_RECORDED_POSITION] ?: 0
            )
        }
    }
}