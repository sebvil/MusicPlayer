package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreBuilder
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTrackRepository : TrackRepository {
    private val defaultTrackInfo = FullTrackInfo(
        track = TrackBuilder.getDefaultTrack().build(),
        artists = listOf(ArtistBuilder.getDefaultArtist().build()),
        album = AlbumBuilder.getDefaultAlbum().build(),
        genres = listOf(GenreBuilder.getDefaultGenre().build())
    )

    private val secondaryTrackInfo = FullTrackInfo(
        track = TrackBuilder.getSecondaryTrack().build(),
        artists = listOf(ArtistBuilder.getSecondaryArtist().build()),
        album = AlbumBuilder.getSecondaryAlbum().build(),
        genres = listOf(GenreBuilder.getSecondaryGenre().build())
    )


    private val tracksMap = mapOf(
        TrackBuilder.DEFAULT_TRACK_ID to defaultTrackInfo,
        TrackBuilder.SECONDARY_TRACK_ID to secondaryTrackInfo
    )

    private val artistMap = mapOf(
        ArtistBuilder.DEFAULT_ARTIST_NAME to listOf(defaultTrackInfo),
        ArtistBuilder.SECONDARY_ARTIST_NAME to listOf(secondaryTrackInfo),
    )

    private val albumMap = mapOf(
        AlbumBuilder.DEFAULT_ALBUM_ID to listOf(defaultTrackInfo),
        AlbumBuilder.SECONDARY_ALBUM_ID to listOf(secondaryTrackInfo),
    )

    private val genreMap = mapOf(
        GenreBuilder.DEFAULT_GENRE_NAME to listOf(defaultTrackInfo),
        GenreBuilder.SECONDARY_GENRE_NAME to listOf(secondaryTrackInfo),
    )

    override fun getTracksCount(): Flow<Long> = flow { emit(tracksMap.size.toLong()) }

    override fun getAllTracks(): Flow<List<FullTrackInfo>> =
        flow { emit(tracksMap.values.toList()) }

    override fun getTrack(tracksId: String): Flow<FullTrackInfo> =
        flow { tracksMap[tracksId]?.also { emit(it) } }

    override fun getTracksForArtist(artistName: String): Flow<List<FullTrackInfo>> =
        flow { artistMap[artistName]?.also { emit(it) } }

    override fun getTracksForAlbum(albumId: String): Flow<List<FullTrackInfo>> =
        flow { albumMap[albumId]?.also { emit(it) } }


    override fun getTracksForGenre(genreName: String): Flow<List<FullTrackInfo>> =
        flow { genreMap[genreName]?.also { emit(it) } }

    override fun getTracksForQueue(mediaGroup: MediaGroup): Flow<List<FullTrackInfo>> = flow {  }

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
