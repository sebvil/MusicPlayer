package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTrackRepository(
    private val tracks: List<FullTrackInfo> = listOf(),
    private val playlistTrackCrossRefs: List<PlaylistTrackCrossRef> = listOf(),
    private val queueTrackCrossRefs: List<MediaQueueTrackCrossRef> = listOf(),
) : TrackRepository {

    override fun getTracksCount(): Flow<Long> = flow { emit(tracks.size.toLong()) }

    override fun getAllTracks(): Flow<List<Track>> = flow { emit(tracks.map { it.track }) }

    override fun getTrack(tracksId: String): Flow<FullTrackInfo> = flow {
        tracks.find { it.track.trackId == tracksId }?.also { emit(it) }
    }

    override fun getTracks(tracksIds: List<String>): Flow<List<Track>> = flow {
        emit(tracks.map { it.track }.filter { it.trackId in tracksIds })
    }

    override fun getTracksForArtist(artistName: String): Flow<List<Track>> = flow {
        emit(tracks.filter { artistName in it.artists }.map { it.track })
    }

    override fun getTracksForAlbum(albumId: String): Flow<List<Track>> = flow {
        emit(tracks.filter { albumId === it.track.albumId }.map { it.track })
    }


    override fun getTracksForGenre(genreName: String): Flow<List<Track>> = flow {
        emit(tracks.filter { genreName in it.genres }.map { it.track })
    }

    override fun getTracksForPlaylist(playlistName: String): Flow<List<Track>> = flow {
        emit(tracks.filter {
            it.track.trackId in playlistTrackCrossRefs.filter { xref -> xref.playlistName == playlistName }
                .map { xref -> xref.trackId }
        }.map { it.track })
    }

    override fun getTracksForQueue(mediaGroup: MediaGroup): Flow<List<Track>> = flow {
        emit(tracks.filter {
            it.track.trackId in queueTrackCrossRefs.filter { xref ->
                xref.mediaGroupType == mediaGroup.mediaGroupType && xref.groupMediaId == mediaGroup.mediaId
            }.map { xref -> xref.trackId }
        }.map { it.track })
    }

    override suspend fun insertAllTracks(
        tracks: Set<Track>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<Artist>,
        genres: Set<Genre>,
        albums: Set<Album>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>
    ) = Unit
}
