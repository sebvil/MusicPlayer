package com.sebastianvm.musicplayer.repository

import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.extensions.id
import com.sebastianvm.musicplayer.util.extensions.toMediaMetadataCompat
import com.sebastianvm.musicplayer.util.getStringComparator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaQueueRepository @Inject constructor(
    private val mediaQueueDao: MediaQueueDao,
    private val trackRepository: TrackRepository
) {
    private suspend fun createQueue(mediaGroup: MediaGroup, trackIds: List<String>): Long {
        val queueId = mediaQueueDao.insertQueue(
            MediaQueue(
                mediaType = mediaGroup.mediaType,
                groupMediaId = mediaGroup.mediaId
            )
        )
        mediaQueueDao.insertMediaQueueTrackCrossRefs(trackIds.mapIndexed { index, trackId ->
            MediaQueueTrackCrossRef(
                mediaType = mediaGroup.mediaType,
                groupMediaId = mediaGroup.mediaId,
                trackId = trackId,
                trackIndex = index
            )
        })
        return queueId
    }

    suspend fun createQueue(
        mediaGroup: MediaGroup,
        sortOption: SortOption,
        sortOrder: SortOrder
    ): Long {
        val trackIds = when (mediaGroup.mediaType) {
            MediaType.TRACK -> trackRepository.getAllTracks()
            MediaType.ARTIST -> trackRepository.getTracksForArtist(mediaGroup.mediaId)
            MediaType.ALBUM -> trackRepository.getTracksForAlbum(mediaGroup.mediaId)
            MediaType.GENRE -> trackRepository.getTracksForGenre(mediaGroup.mediaId)
        }.map { tracks ->
            tracks.map {
                it.toMediaMetadataCompat()
            }.sortedWith(getTrackComparator(sortOrder, sortOption.metadataKey)).mapNotNull { it.id }
        }.first()

        return createQueue(mediaGroup, trackIds)

    }

    private fun getTrackComparator(
        sortOrder: SortOrder,
        sortKey: String
    ): Comparator<MediaMetadataCompat> {
        return when (sortKey) {
            MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER -> compareBy<MediaMetadataCompat> {
                it.getLong(sortKey)
            }
            else -> getStringComparator(sortOrder) { metadata ->
                metadata.getString(sortKey)
            }
        }
    }

}