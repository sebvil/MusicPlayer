//package com.sebastianvm.musicplayer.repository.track
//
//import com.sebastianvm.musicplayer.database.entities.Album
//import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
//import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
//import com.sebastianvm.musicplayer.database.entities.Artist
//import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
//import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
//import com.sebastianvm.musicplayer.database.entities.Genre
//import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
//import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
//import com.sebastianvm.musicplayer.database.entities.Track
//import com.sebastianvm.musicplayer.player.MediaGroup
//import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
//import com.sebastianvm.musicplayer.util.sort.SortOptions
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.mapNotNull
//
//class FakeTrackRepository(
//    tracks: List<FullTrackInfo> = listOf(),
//    private val queueTrackCrossRefs: List<MediaQueueTrackCrossRef> = listOf(),
//) : TrackRepository {
//
//    private val tracksState = MutableStateFlow(tracks)
//
//    override fun getTracksCount(): Flow<Int> = tracksState.map { it.size }
//
//    override fun getAllTracks(mediaSortPreferences: MediaSortPreferences<TrackListSortOptions>): Flow<List<Track>> =
//        tracksState.map { tracks -> tracks.map { it.track } }
//
//    override fun getTrack(trackId: String): Flow<FullTrackInfo> =
//        tracksState.mapNotNull { tracks -> tracks.find { it.track.trackId == trackId } }
//
//    override fun getTracks(tracksIds: List<String>): Flow<List<Track>> =
//        tracksState.map { tracks ->
//            tracks.filter { it.track.trackId in tracksIds }
//                .map { it.track }
//        }
//
//    override fun getTracksForArtist(artistName: String): Flow<List<Track>> =
//        tracksState.map { tracks ->
//            tracks.filter { artistName in it.artists }.map { it.track }
//        }
//
//    override fun getTracksForAlbum(albumId: String): Flow<List<Track>> =
//        tracksState.map { tracks -> tracks.filter { it.track.albumId == albumId }.map { it.track } }
//
//
//    override fun getTracksForGenre(
//        genreName: String,
//        mediaSortPreferences: MediaSortPreferences<TrackListSortOptions>
//    ): Flow<List<Track>> =
//        tracksState.map { tracks ->
//            tracks.filter { it.genres.contains(genreName) }.map { it.track }
//        }
//
//    override fun getTracksForPlaylist(playlistName: String): Flow<List<Track>> =
//        tracksState.map { tracks ->
//            tracks.filter { it.playlists.contains(playlistName) }.map { it.track }
//        }
//
//    override fun getTracksForQueue(mediaGroup: MediaGroup): Flow<List<Track>> =
//        tracksState.map { tracks ->
//            tracks.filter {
//                it.track.trackId in queueTrackCrossRefs.filter { xref ->
//                    xref.mediaGroupType == mediaGroup.mediaGroupType && xref.groupMediaId == mediaGroup.mediaId
//                }.map { xref -> xref.trackId }
//            }.map { it.track }
//        }
//
//    override suspend fun insertAllTracks(
//        tracks: Set<Track>,
//        artistTrackCrossRefs: Set<ArtistTrackCrossRef>,
//        genreTrackCrossRefs: Set<GenreTrackCrossRef>,
//        artists: Set<Artist>,
//        genres: Set<Genre>,
//        albums: Set<Album>,
//        albumsForArtists: Set<AlbumsForArtist>,
//        appearsOnForArtists: Set<AppearsOnForArtist>
//    ) = Unit
//}
