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
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow

interface TrackRepository {

    fun getTracksCount(): Flow<Long>

    fun getAllTracks(): Flow<List<FullTrackInfo>>

    fun getTrack(tracksId: String): Flow<FullTrackInfo>

    fun getTracksForArtist(artistName: String): Flow<List<FullTrackInfo>>

    fun getTracksForAlbum(albumId: String): Flow<List<FullTrackInfo>>

    fun getTracksForGenre(genreName: String): Flow<List<FullTrackInfo>>

    fun getTracksForQueue(mediaGroup: MediaGroup): Flow<List<FullTrackInfo>>

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
