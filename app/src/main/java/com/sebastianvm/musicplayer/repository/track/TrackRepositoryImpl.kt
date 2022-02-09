package com.sebastianvm.musicplayer.repository.track

import com.sebastianvm.musicplayer.database.daos.TrackDao
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
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrackRepositoryImpl@Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val trackDao: TrackDao
): TrackRepository {

    override fun getTracksCount(): Flow<Long> {
        return trackDao.getTracksCount().distinctUntilChanged()
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return trackDao.getAllTracks().distinctUntilChanged()
    }

    override fun getTrack(tracksId: String): Flow<FullTrackInfo> {
        return trackDao.getTrack(tracksId).distinctUntilChanged()
    }

    override fun getTracks(tracksIds: List<String>): Flow<List<Track>> {
        return trackDao.getTracks(tracksIds).distinctUntilChanged()
    }

    override fun getTracksForArtist(artistName: String) : Flow<List<Track>> {
        return trackDao.getTracksForArtist(artistName).distinctUntilChanged()
    }

    override fun getTracksForAlbum(albumId: String) : Flow<List<Track>> {
        return trackDao.getTracksForAlbum(albumId).distinctUntilChanged()
    }

    override fun getTracksForGenre(genreName: String) : Flow<List<Track>> {
        return trackDao.getTracksForGenre(genreName).distinctUntilChanged()
    }

    override fun getTracksForPlaylist(playlistName: String): Flow<List<Track>> {
        return trackDao.getTracksForPlaylist(playlistName).distinctUntilChanged()
    }

    override fun getTracksForQueue(mediaGroup: MediaGroup): Flow<List<Track>> {
        return trackDao.getTracksForQueue(mediaGroup.mediaGroupType, mediaGroup.mediaId).distinctUntilChanged()
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
        withContext(ioDispatcher) {
            trackDao.insertAllTracks(
                tracks = tracks,
                artistTrackCrossRefs = artistTrackCrossRefs,
                genreTrackCrossRefs = genreTrackCrossRefs,
                artists = artists,
                genres = genres,
                albums = albums,
                albumsForArtists = albumsForArtists,
                appearsOnForArtists = appearsOnForArtists,
            )
        }
    }
}
