package com.sebastianvm.musicplayer.repository.track

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
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun getTracksCount(): Flow<Int>

    fun getAllTracks(): Flow<List<Track>>

    fun getTrack(trackId: Long): Flow<TrackWithArtists>

    fun getTracksForArtist(artistId: Long): Flow<List<Track>>

    fun getTracksForAlbum(albumId: Long): Flow<List<Track>>

    fun getTracksForGenre(genreId: Long): Flow<List<Track>>

    fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>>

    fun getTracksForMedia(
        trackListType: TrackListType,
        mediaId: Long
    ): Flow<List<ModelListItemState>>

    fun getTrackListMetadata(trackListType: TrackListType, mediaId: Long): Flow<TrackListMetadata>

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
