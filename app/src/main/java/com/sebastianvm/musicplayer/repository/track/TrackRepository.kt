package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun getTracksCount(): Flow<Int>

    fun getAllTracks(mediaSortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>): Flow<List<Track>>

    fun getTrack(trackId: String): Flow<FullTrackInfo>

    fun getTracks(tracksIds: List<String>): Flow<List<Track>>

    fun getTracksForArtist(artistName: String): Flow<List<Track>>

    fun getTracksForAlbum(albumId: String): Flow<List<Track>>

    fun getTracksForGenre(genreName: String, mediaSortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>): Flow<List<Track>>

    fun getTracksForPlaylist(playlistName: String): Flow<List<Track>>

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
