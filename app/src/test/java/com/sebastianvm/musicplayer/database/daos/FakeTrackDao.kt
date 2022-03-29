package com.sebastianvm.musicplayer.database.daos

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroupType
import kotlinx.coroutines.flow.Flow

class FakeTrackDao : TrackDao {
    override fun getTracksCount(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun getAllTracks(): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun getTracks(trackIds: List<String>): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun getTrack(trackId: String): Flow<FullTrackInfo> {
        TODO("Not yet implemented")
    }

    override fun getTracksForArtist(artistName: String): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun getTracksForAlbum(albumId: String): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun getTracksForGenre(genreName: String): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun getTracksForPlaylist(playlistName: String): Flow<List<Track>> {
        TODO("Not yet implemented")
    }

    override fun getTracksForQueue(
        mediaType: MediaGroupType,
        groupMediaId: String
    ): Flow<List<Track>> {
        TODO("Not yet implemented")
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
    ) {
        TODO("Not yet implemented")
    }
}