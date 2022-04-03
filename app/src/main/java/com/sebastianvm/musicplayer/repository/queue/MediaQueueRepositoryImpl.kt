package com.sebastianvm.musicplayer.repository.queue

import android.content.Context
import androidx.media3.common.C
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.extensions.withUpdatedIndices
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.getStringComparator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MediaQueueRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val mediaQueueDao: MediaQueueDao,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val preferencesRepository: SortPreferencesRepositoryImpl,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : MediaQueueRepository {
    private suspend fun createQueue(
        mediaGroup: MediaGroup,
        trackIds: List<String>,
        queueName: String
    ): Long {
        return withContext(ioDispatcher) {
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
            queueId
        }
    }

    override suspend fun createQueue(
        mediaGroup: MediaGroup,
    ): Long {
        return withContext(ioDispatcher) {
            val queueName: String
            val sortOption: MediaSortOption
            val sortOrder: MediaSortOrder
            val trackIds = when (mediaGroup.mediaGroupType) {
                MediaGroupType.ALL_TRACKS -> {
                    queueName = ResUtil.getString(context = context, R.string.all_songs)
                    preferencesRepository.getTracksListSortOptions(TracksListType.ALL_TRACKS, tracksListName = "")
                        .first().also {
                            sortOption = it.sortOption
                            sortOrder = it.sortOrder
                        }
                    trackRepository.getAllTracks()
                }
                MediaGroupType.ARTIST -> {
                    queueName = mediaGroup.mediaId
                    sortOption = MediaSortOption.TRACK
                    sortOrder = MediaSortOrder.ASCENDING
                    trackRepository.getTracksForArtist(mediaGroup.mediaId)
                }
                MediaGroupType.ALBUM -> {
                    queueName = albumRepository.getAlbum(mediaGroup.mediaId).first().album.albumName
                    sortOption = MediaSortOption.TRACK_NUMBER
                    sortOrder = MediaSortOrder.ASCENDING
                    trackRepository.getTracksForAlbum(mediaGroup.mediaId)
                }
                MediaGroupType.GENRE -> {
                    queueName = mediaGroup.mediaId
                    preferencesRepository.getTracksListSortOptions(
                        TracksListType.GENRE,
                        mediaGroup.mediaId
                    )
                        .first().also {
                            sortOption = it.sortOption
                            sortOrder = it.sortOrder
                        }
                    trackRepository.getTracksForGenre(mediaGroup.mediaId)
                }
                MediaGroupType.SINGLE_TRACK -> {
                    val track = trackRepository.getTrack(mediaGroup.mediaId)
                    queueName = track.first().track.trackName
                    sortOption = MediaSortOption.TRACK
                    sortOrder = MediaSortOrder.ASCENDING
                    track.map { listOf(it.track) }
                }
                MediaGroupType.PLAYLIST -> {
                    queueName = mediaGroup.mediaId
                    preferencesRepository.getTracksListSortOptions(
                        TracksListType.PLAYLIST,
                        mediaGroup.mediaId
                    )
                        .first().also {
                            sortOption = it.sortOption
                            sortOrder = it.sortOrder
                        }
                    trackRepository.getTracksForPlaylist(mediaGroup.mediaId)
                }
                MediaGroupType.UNKNOWN -> {
                    queueName = ""
                    sortOption = MediaSortOption.TRACK
                    sortOrder = MediaSortOrder.ASCENDING
                    flow { }
                }
            }.map { tracks ->
                tracks.sortedWith(getTrackComparator(sortOrder, sortOption))
                    .map { it.trackId }
            }.first()
            createQueue(mediaGroup, trackIds, queueName)
        }
    }

    override suspend fun insertOrUpdateMediaQueueTrackCrossRefs(
        queue: MediaGroup,
        mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>
    ) {
        withContext(ioDispatcher) {
            mediaQueueDao.insertOrUpdateMediaQueueTrackCrossRefs(
                queueId = queue.mediaId,
                mediaGroupType = queue.mediaGroupType,
                mediaQueueTrackCrossRefs = mediaQueueTrackCrossRefs
            )
        }
    }

    private fun getTrackComparator(
        sortOrder: MediaSortOrder,
        sortOption: MediaSortOption
    ): Comparator<Track> {
        return when (sortOption) {
            MediaSortOption.ALBUM -> getStringComparator(sortOrder) { track -> track.albumName }
            MediaSortOption.TRACK -> getStringComparator(sortOrder) { track -> track.trackName }
            MediaSortOption.ARTIST -> getStringComparator(sortOrder) { track -> track.artists }
            MediaSortOption.TRACK_NUMBER -> compareBy { track -> track.trackNumber }
            else -> throw IllegalStateException("Invalid sort option for tracks")
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
        return withContext(ioDispatcher) {
            val queue = mediaPlaybackRepository.getSavedPlaybackInfo()
                .first().currentQueue.takeUnless { it.mediaGroupType == MediaGroupType.UNKNOWN }
                ?: return@withContext false
            val index = mediaPlaybackRepository.addToQueue(trackIds)
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
            true
        }
    }


}
