package com.sebastianvm.musicplayer.player

import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.GenreRepository
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.MusicRepository
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
class BrowseTree @Inject constructor(
    musicRepository: MusicRepository,
    private val trackRepository: TrackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    artistRepository: ArtistRepository,
    genreRepository: GenreRepository,
    albumRepository: AlbumRepository,
) {

    private val tree = mutableMapOf<String, Flow<MutableSet<MediaMetadataCompat>>>()
    private val counts = musicRepository.getCounts()
    private val tracks = trackRepository.getAllTracks()
    private val artists = artistRepository.getArtistsWithAlbums()
    private val albums = albumRepository.getAlbums()
    private val genres = genreRepository.getGenresWithTracks()

//    init {
//        CoroutineScope(Dispatchers.IO).launch {
//            tree[MEDIA_ROOT] = counts.map { libraryCounts ->
//                val tracksRootMetadata = MediaMetadataCompat.Builder().apply {
//                    id = TRACKS_ROOT
//                    title = R.string.all_songs.toString()
//                    iconRes = R.drawable.ic_song
//                    counts = libraryCounts.tracks
//                    flags =
//                        MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
//                }.build()
//                val artistsRootMetadata = MediaMetadataCompat.Builder().apply {
//                    id = ARTISTS_ROOT
//                    title = R.string.artists.toString()
//                    iconRes = R.drawable.ic_artist
//                    counts = libraryCounts.artists
//                    flags =
//                        MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
//                }.build()
//                val albumsRootMetadata = MediaMetadataCompat.Builder().apply {
//                    id = ALBUMS_ROOT
//                    title = R.string.albums.toString()
//                    iconRes = R.drawable.ic_album
//                    counts = libraryCounts.albums
//                    flags =
//                        MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
//                }.build()
//                val genresRootMetadata = MediaMetadataCompat.Builder().apply {
//                    id = GENRES_ROOT
//                    title = R.string.genres.toString()
//                    iconRes = R.drawable.ic_genre
//                    counts = libraryCounts.genres
//                    flags =
//                        MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
//                }.build()
//
//                mutableSetOf(
//                    tracksRootMetadata,
//                    artistsRootMetadata,
//                    albumsRootMetadata,
//                    genresRootMetadata
//                )
//            }
//            tree[TRACKS_ROOT] = tracks.map { tracks ->
//                tracks.map { it.toMediaMetadataCompat() }.toMutableSet()
//            }
//
//            tree[ARTISTS_ROOT] = artists.map { artistWithAlbums ->
//                artistWithAlbums.forEach { artist ->
//                    tree["artist-${artist.artist.artistName}"] = combine(
//                        albumRepository.getAlbums(artist.artistAlbums.map { album -> album.albumId }),
//                        albumRepository.getAlbums(artist.artistAppearsOn.map { album -> album.albumId })
//                    ) { albums, appearsOn ->
//
//                        albums.map { it.toMediaMetadataCompat(AlbumType.ALBUM) }
//                            .plus(appearsOn.map { it.toMediaMetadataCompat(AlbumType.APPEARS_ON) })
//                            .toMutableSet()
//                    }
//                }
//                artistWithAlbums.map { it.artist.toMediaMetadataCompat() }.toMutableSet()
//            }
//
//            tree[ALBUMS_ROOT] = albums.map { fullAlbumInfoList ->
//
//                fullAlbumInfoList.forEach { album ->
//                    tree["album-${album.album.albumId}"] =
//                        trackRepository.getTracks(album.tracks.map { it.trackId }).map { tracks ->
//                            tracks.map { it.toMediaMetadataCompat() }.toMutableSet()
//                        }
//                }
//                fullAlbumInfoList.map { it.toMediaMetadataCompat() }.toMutableSet()
//
//            }
//
//            tree[GENRES_ROOT] = genres.map { genresList ->
//
//                genresList.forEach { genre ->
//                    tree["genre-${genre.genre.genreName}"] =
//                        trackRepository.getTracks(genre.tracks.map { it.trackId }).map { tracks ->
//                            tracks.map { it.toMediaMetadataCompat() }.toMutableSet()
//                        }
//                }
//                genresList.map { it.genre.toMediaMetadataCompat() }.toMutableSet()
//
//            }
//        }
//    }

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
