package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.repository.TrackRepository
import com.sebastianvm.musicplayer.util.extensions.toMediaMetadataCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import javax.inject.Singleton

@Parcelize
enum class MediaType : Parcelable {
    ALL_TRACKS,
    ARTIST,
    ALBUM,
    GENRE,
    SINGLE_TRACK
}

@Parcelize
data class MediaGroup(val mediaType: MediaType, val mediaId: String) : Parcelable

@Singleton
class BrowseTree @Inject constructor(private val trackRepository: TrackRepository) {

    private val tree = mutableMapOf<String, Flow<MutableSet<MediaMetadataCompat>>>()


    operator fun get(mediaId: String) = tree[mediaId]

    fun getTracksList(mediaGroup: MediaGroup): Flow<List<MediaMetadataCompat>> {
        return trackRepository.getTracksForQueue(mediaGroup).map { tracks ->
            tracks.map {
                it.toMediaMetadataCompat()
            }
        }
    }


    companion object {
        const val MEDIA_ROOT = "MEDIA_ROOT"
        const val TRACKS_ROOT = "TRACKS_ROOT"
        const val ARTISTS_ROOT = "ARTISTS_ROOT"
        const val ALBUMS_ROOT = "ALBUMS_ROOT"
        const val GENRES_ROOT = "GENRES_ROOT"
    }
}
