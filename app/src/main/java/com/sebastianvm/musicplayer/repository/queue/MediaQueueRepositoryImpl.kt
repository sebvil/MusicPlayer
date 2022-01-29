package com.sebastianvm.musicplayer.repository.queue

import android.content.Context
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MediaQueueRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaQueueDao: MediaQueueDao,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository
) : MediaQueueRepository {
    private suspend fun createQueue(
        mediaGroup: MediaGroup,
        trackIds: List<String>,
        queueName: String
    ): Long {
        val queueId = mediaQueueDao.insertQueue(
            MediaQueue(
                mediaType = mediaGroup.mediaType,
                groupMediaId = mediaGroup.mediaId,
                queueName = queueName,
            )
        )
        mediaQueueDao.insertOrUpdateMediaQueueTrackCrossRefs(trackIds.mapIndexed { index, trackId ->
            MediaQueueTrackCrossRef(
                mediaType = mediaGroup.mediaType,
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
        val trackIds = when (mediaGroup.mediaType) {
            MediaType.ALL_TRACKS -> {
                queueName = ResUtil.getString(context = context, R.string.all_songs)
                trackRepository.getAllTracks()
            }
            MediaType.ARTIST -> {
                queueName = mediaGroup.mediaId
                trackRepository.getTracksForArtist(mediaGroup.mediaId)
            }
            MediaType.ALBUM -> {
                queueName = albumRepository.getAlbum(mediaGroup.mediaId).first().album.albumName
                trackRepository.getTracksForAlbum(mediaGroup.mediaId)
            }
            MediaType.GENRE -> {
                queueName = mediaGroup.mediaId
                trackRepository.getTracksForGenre(mediaGroup.mediaId)
            }
            MediaType.SINGLE_TRACK -> {
                val track = trackRepository.getTrack(mediaGroup.mediaId)
                queueName = track.first().track.trackName
                track.map { listOf(it) }
            }
            MediaType.PLAYLIST -> {
                queueName = mediaGroup.mediaId
                trackRepository.getTracksForPlaylist(mediaGroup.mediaId)
            }
            MediaType.UNKNOWN -> {
                queueName = ""
                flow { }
            }
        }.map { tracks ->
            tracks.sortedWith(getTrackComparator(sortOrder, sortOption)).map { it.track.trackId }
        }.first()

        return createQueue(mediaGroup, trackIds, queueName)
    }

    override suspend fun insertOrUpdateMediaQueueTrackCrossRefs(mediaQueueTrackCrossRefs: List<MediaQueueTrackCrossRef>) {
        mediaQueueDao.insertOrUpdateMediaQueueTrackCrossRefs(mediaQueueTrackCrossRefs)
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
        return mediaQueueDao.getQueue(mediaGroup.mediaId, mediaGroup.mediaType)
            .distinctUntilChanged()
    }

}
