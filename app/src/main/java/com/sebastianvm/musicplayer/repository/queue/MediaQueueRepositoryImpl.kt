package com.sebastianvm.musicplayer.repository.queue

import android.content.Context
import androidx.media3.common.C
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.extensions.withUpdatedIndices
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaQueueRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaQueueDao: MediaQueueDao,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val preferencesRepository: PreferencesRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : MediaQueueRepository {
    private suspend fun createQueue(
        mediaGroup: MediaGroup,
        trackIds: List<String>,
        queueName: String
    ): Long {
        val queueId = mediaQueueDao.insertQueue(
            MediaQueue(
                mediaGroupType = mediaGroup.mediaGroupType,
                groupMediaId = mediaGroup.mediaId,
                queueName = queueName,
            )
        )
        mediaQueueDao.insertOrUpdateMediaQueueTrackCrossRefs(
            queueId = mediaGroup.mediaId,
            mediaGroupType = mediaGroup.mediaGroupType,
            trackIds.mapIndexed { index, trackId ->
                MediaQueueTrackCrossRef(
                    mediaGroupType = mediaGroup.mediaGroupType,
                    groupMediaId = mediaGroup.mediaId,
                    trackId = trackId,
                    trackIndex = index
                )
            })
        return queueId
    }

    override suspend fun createQueue(
        mediaGroup: MediaGroup,
        sortOption: SortOption,
        sortOrder: SortOrder
    ): Long {
        val queueName: String
        val trackIds = when (mediaGroup.mediaGroupType) {
            MediaGroupType.ALL_TRACKS -> {
                queueName = ResUtil.getString(context = context, R.string.all_songs)
                trackRepository.getAllTracks()
            }
            MediaGroupType.ARTIST -> {
                queueName = mediaGroup.mediaId
                trackRepository.getTracksForArtist(mediaGroup.mediaId)
            }
            MediaGroupType.ALBUM -> {
                queueName = albumRepository.getAlbum(mediaGroup.mediaId).first().album.albumName
                trackRepository.getTracksForAlbum(mediaGroup.mediaId)
            }
            MediaGroupType.GENRE -> {
                queueName = mediaGroup.mediaId
                trackRepository.getTracksForGenre(mediaGroup.mediaId)
            }
            MediaGroupType.SINGLE_TRACK -> {
                val track = trackRepository.getTrack(mediaGroup.mediaId)
                queueName = track.first().track.trackName
                track.map { listOf(it) }
            }
            MediaGroupType.PLAYLIST -> {
                queueName = mediaGroup.mediaId
                trackRepository.getTracksForPlaylist(mediaGroup.mediaId)
            }
            MediaGroupType.UNKNOWN -> {
                queueName = ""
                flow { }
            }
        }.map { tracks ->
            tracks.sortedWith(getTrackComparator(sortOrder, sortOption)).map { it.track.trackId }
        }.first()

        return createQueue(mediaGroup, trackIds, queueName)
    }

    override suspend fun insertOrUpdateMediaQueueTrackCrossRefs(
        queue: MediaGroup,
        mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>
    ) {
        mediaQueueDao.insertOrUpdateMediaQueueTrackCrossRefs(
            queueId = queue.mediaId,
            mediaGroupType = queue.mediaGroupType,
            mediaQueueTrackCrossRefs = mediaQueueTrackCrossRefs
        )
    }

    private fun getTrackComparator(
        sortOrder: SortOrder,
        sortOption: SortOption
    ): Comparator<FullTrackInfo> {
        return when (sortOption) {
            SortOption.ALBUM_NAME -> getStringComparator(sortOrder) { track -> track.album.albumName }
            SortOption.TRACK_NAME -> getStringComparator(sortOrder) { track -> track.track.trackName }
            SortOption.ARTIST_NAME -> getStringComparator(sortOrder) { track -> track.artists.toString() }
            SortOption.YEAR -> compareBy { track -> track.album.year }
            SortOption.TRACK_NUMBER -> compareBy { track -> track.track.trackNumber }
        }
    }

    override fun getAllQueues(): Flow<List<MediaQueue>> {
        return mediaQueueDao.getAllQueues().distinctUntilChanged()
    }

    override fun getQueue(mediaGroup: MediaGroup): Flow<MediaQueue> {
        return mediaQueueDao.getQueue(mediaGroup.mediaId, mediaGroup.mediaGroupType)
            .distinctUntilChanged()
    }

    override fun getMediaQueTrackCrossRefs(queue: MediaGroup): Flow<List<MediaQueueTrackCrossRef>> {
        return mediaQueueDao.getMediaQueTrackCrossRefs(
            queue.mediaId,
            queue.mediaGroupType
        ).distinctUntilChanged()
    }

    override suspend fun addToQueue(trackIds: List<String>): Boolean {
        val queue = preferencesRepository.getSavedPlaybackInfo()
            .first().currentQueue.takeUnless { it.mediaGroupType == MediaGroupType.UNKNOWN }
            ?: return false
        val index = mediaPlaybackRepository.addToQueue(trackIds)
        withContext(Dispatchers.IO) {
            val queueItems = getMediaQueTrackCrossRefs(queue).first().toMutableList()
            if (index == C.INDEX_UNSET) {
                queueItems.addAll(trackIds.map {
                    MediaQueueTrackCrossRef(
                        mediaGroupType = queue.mediaGroupType,
                        groupMediaId = queue.mediaId,
                        trackId = it,
                        trackIndex = -1
                    )
                })
            } else {
                queueItems.addAll(index, trackIds.map {
                    MediaQueueTrackCrossRef(
                        mediaGroupType = queue.mediaGroupType,
                        groupMediaId = queue.mediaId,
                        trackId = it,
                        trackIndex = -1
                    )
                })
            }
            insertOrUpdateMediaQueueTrackCrossRefs(queue, queueItems.withUpdatedIndices())
        }
        return true
    }


}
