package com.sebastianvm.musicplayer.player

import android.media.browse.MediaBrowser
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.*
import com.sebastianvm.musicplayer.repository.*
import com.sebastianvm.musicplayer.ui.util.mvvm.NonNullMediatorLiveData
import com.sebastianvm.musicplayer.util.AlbumType
import com.sebastianvm.musicplayer.util.extensions.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.set

typealias MediaBrowseTree = MutableMap<String, MutableSet<MediaMetadataCompat>>

typealias ArtistMap = Map<Artist, Pair<List<AlbumWithArtists>, List<AlbumWithArtists>>>

/**
 * Tree for browsing the music library.
 * Titles are resources for top level directories, strings for everything else.
 */
@Singleton
class BrowseTree @Inject constructor(
    musicRepository: MusicRepository,
    trackRepository: TrackRepository,
    artistRepository: ArtistRepository,
    genreRepository: GenreRepository,
    albumRepository: AlbumRepository,
) : LiveData<MediaBrowseTree>() {


    private val tree = mutableMapOf<String, MutableSet<MediaMetadataCompat>>()
    private val counts = musicRepository.getCounts()
    private val tracks = trackRepository.getAllTracks()
    private val artists = artistRepository.getArtists()
    private val albums = albumRepository.getAlbums()
    private val genres = genreRepository.getGenres()

    private val artistWithAlbums =
        Transformations.switchMap(artists) { artists ->
            val mediatorLiveData =
                NonNullMediatorLiveData<ArtistMap>(
                    artists.associate { it.artist to Pair(listOf(), listOf()) }
                )
            artists.forEach { artist ->
                mediatorLiveData.addSource(albumRepository.getAlbums(artist.artistAlbums.map { album -> album.albumGid })) { albums ->
                    val artistAlbumMap = mediatorLiveData.value.toMutableMap()
                    val value = artistAlbumMap[artist.artist] ?: Pair(listOf(), listOf())
                    artistAlbumMap[artist.artist] = value.copy(first = albums)
                    mediatorLiveData.value = artistAlbumMap
                }
                mediatorLiveData.addSource(albumRepository.getAlbums(artist.artistAppearsOn.map { album -> album.albumGid })) { albums ->
                    val artistAlbumMap = mediatorLiveData.value.toMutableMap()
                    val value = artistAlbumMap[artist.artist] ?: Pair(listOf(), listOf())
                    artistAlbumMap[artist.artist] = value.copy(second = albums)
                    mediatorLiveData.value = artistAlbumMap
                }
            }
            mediatorLiveData
        }
    private val genresWithTracks = Transformations.switchMap(genres) { genres ->
        val mediatorLiveData =
            NonNullMediatorLiveData(
                genres.associate { it.genre to listOf<FullTrackInfo>() }
            )
        genres.forEach { genre ->
            mediatorLiveData.addSource(trackRepository.getTracks(genre.tracks.map { it.trackGid })) {
                val value = mediatorLiveData.value.toMutableMap()
                value[genre.genre] = it
                mediatorLiveData.value = value
            }
        }
        mediatorLiveData
    }
    private val albumsWithTracks =
        Transformations.switchMap(albums) { albums ->
            val mediatorLiveData =
                NonNullMediatorLiveData(
                    albums.associateWith { listOf<FullTrackInfo>() }
                )
            albums.forEach { album ->
                mediatorLiveData.addSource(trackRepository.getTracks(album.tracks.map { it.trackGid })) {
                    val value = mediatorLiveData.value.toMutableMap()
                    value[album] = it
                    mediatorLiveData.value = value
                }
            }
            mediatorLiveData
        }

    private val updateValue = { newTree: MediaBrowseTree ->
        value = newTree
    }

    override fun onActive() {
        counts.observeForever(rootObserver)
        tracks.observeForever(trackObserver)
        artistWithAlbums.observeForever(artistsObserver)
        albumsWithTracks.observeForever(albumsObserver)
        genresWithTracks.observeForever(genresObserver)
    }

    override fun onInactive() {
        counts.removeObserver(rootObserver)
        tracks.removeObserver(trackObserver)
        artistWithAlbums.removeObserver(artistsObserver)
        albumsWithTracks.removeObserver(albumsObserver)
        genresWithTracks.removeObserver(genresObserver)
    }


    private val rootObserver =
        Observer<MusicRepository.CountHolder> { libraryCounts ->
            val tracksRootMetadata = MediaMetadataCompat.Builder().apply {
                id = TRACKS_ROOT
                title = R.string.all_songs.toString()
                iconRes = R.drawable.ic_song
                counts = libraryCounts.tracks
                flags =
                    MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
            }.build()
            val artistsRootMetadata = MediaMetadataCompat.Builder().apply {
                id = ARTISTS_ROOT
                title = R.string.artists.toString()
                iconRes = R.drawable.ic_artist
                counts = libraryCounts.artists
                flags =
                    MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
            }.build()
            val albumsRootMetadata = MediaMetadataCompat.Builder().apply {
                id = ALBUMS_ROOT
                title = R.string.albums.toString()
                iconRes = R.drawable.ic_album
                counts = libraryCounts.albums
                flags =
                    MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
            }.build()
            val genresRootMetadata = MediaMetadataCompat.Builder().apply {
                id = GENRES_ROOT
                title = R.string.genres.toString()
                iconRes = R.drawable.ic_genre
                counts = libraryCounts.genres
                flags =
                    MediaBrowser.MediaItem.FLAG_BROWSABLE or MediaBrowser.MediaItem.FLAG_PLAYABLE
            }.build()

            tree[MEDIA_ROOT] = mutableSetOf(
                tracksRootMetadata,
                artistsRootMetadata,
                albumsRootMetadata,
                genresRootMetadata
            )
            updateValue(tree)
        }

    private val trackObserver =
        Observer<List<FullTrackInfo>> { tracks ->
            tree[TRACKS_ROOT] = tracks.map { it.toMediaMetadataCompat() }.toMutableSet()
            updateValue(tree)
        }

    private val artistsObserver =
        Observer<ArtistMap> { artistsWithAlbums ->
            tree[ARTISTS_ROOT] =
                artistsWithAlbums.keys.map { it.toMediaMetadataCompat() }.toMutableSet()
            artistsWithAlbums.forEach { artistWithAlbums ->
                tree["artist-${artistWithAlbums.key.artistGid}"] =
                    (artistWithAlbums.value.first.map { it.toMediaMetadataCompat(AlbumType.ALBUM) } +
                            artistWithAlbums.value.second.map {
                                it.toMediaMetadataCompat(AlbumType.APPEARS_ON)
                            }).toMutableSet()
            }
            updateValue(tree)
        }

    private val albumsObserver =
        Observer<Map<FullAlbumInfo, List<FullTrackInfo>>> { albumsWithTracks ->
            tree[ALBUMS_ROOT] =
                albumsWithTracks.keys.map { it.toMediaMetadataCompat() }.toMutableSet()
            albumsWithTracks.forEach { albumWithTracks ->
                val album = albumWithTracks.key
                val tracks = albumWithTracks.value
                tree["album-${album.album.albumGid}"] =
                    tracks.map { it.toMediaMetadataCompat() }.toMutableSet()
            }
            updateValue(tree)
        }

    private val genresObserver =
        Observer<Map<Genre, List<FullTrackInfo>>> { genresWithTracks ->
            tree[GENRES_ROOT] =
                genresWithTracks.keys.map { it.toMediaMetadataCompat() }.toMutableSet()
            genresWithTracks.forEach { genreWithTracks ->
                val genre = genreWithTracks.key
                val tracks = genreWithTracks.value
                tree["genre-${genre.genreName}"] =
                    tracks.map { it.toMediaMetadataCompat() }.toMutableSet()
            }
            updateValue(tree)
        }


    operator fun get(mediaId: String) = tree[mediaId]

    companion object {
        const val MEDIA_ROOT = "MEDIA_ROOT"
        const val TRACKS_ROOT = "TRACKS_ROOT"
        const val ARTISTS_ROOT = "ARTISTS_ROOT"
        const val ALBUMS_ROOT = "ALBUMS_ROOT"
        const val GENRES_ROOT = "GENRES_ROOT"
    }
}