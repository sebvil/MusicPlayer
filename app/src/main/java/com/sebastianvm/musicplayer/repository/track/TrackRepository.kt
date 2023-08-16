package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.fakegen.FakeCommandMethod
import com.sebastianvm.fakegen.FakeQueryMethod
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.database.entities.TrackWithArtists
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    @FakeQueryMethod
    fun getTracksCount(): Flow<Int>

    @FakeQueryMethod
    fun getAllTracks(): Flow<List<Track>>

    @FakeQueryMethod
    fun getTrack(trackId: Long): Flow<TrackWithArtists>

    @FakeQueryMethod
    fun getTracksForArtist(artistId: Long): Flow<List<Track>>

    @FakeQueryMethod
    fun getTracksForAlbum(albumId: Long): Flow<List<Track>>

    @FakeQueryMethod
    fun getTracksForGenre(genreId: Long): Flow<List<Track>>

    @FakeQueryMethod
    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    @FakeQueryMethod
    fun getTracksForMedia(trackList: TrackList): Flow<List<ModelListItemState>>

    @FakeQueryMethod
    fun getTrackListMetadata(trackList: TrackList): Flow<TrackListMetadata?>

    @FakeCommandMethod
    suspend fun insertAllTracks(
        tracks: Set<Track>,
        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
        artists: Set<Artist>,
        genres: Set<Genre>,
        albums: Set<Album>,
        albumsForArtists: Set<AlbumsForArtist>,
        appearsOnForArtists: Set<AppearsOnForArtist>
    )
}
