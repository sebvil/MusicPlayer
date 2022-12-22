package com.sebastianvm.musicplayer.repository.playback.mediatree

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_ALBUMS
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_ARTISTS
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_GENRES
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_MIXED
import androidx.media3.common.MediaMetadata.FOLDER_TYPE_TITLES
import com.sebastianvm.musicplayer.player.buildMediaItem
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MediaTree @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
    private val genreRepository: GenreRepository,
    private val playlistRepository: PlaylistRepository
) {

    private val mediaItemsTree: MutableMap<String, List<MediaItem>> = mutableMapOf()
    private val mediaItemsMap: MutableMap<String, Pair<KeyType, MediaItem>> = mutableMapOf()


    private val tracksRoot: MediaItem = buildMediaItem(
        title = "All tracks",
        mediaId = KeyType.TracksRoot.hashCode(),
        isPlayable = false,
        folderType = FOLDER_TYPE_TITLES
    )

    private val albumsRoot = buildMediaItem(
        title = "Albums",
        mediaId = KeyType.AlbumsRoot.hashCode(),
        isPlayable = false,
        folderType = FOLDER_TYPE_ALBUMS
    )

    private val artistsRoot = buildMediaItem(
        title = "Artists",
        mediaId = KeyType.ArtistsRoot.hashCode(),
        isPlayable = false,
        folderType = FOLDER_TYPE_ARTISTS
    )

    private val genresRoot = buildMediaItem(
        title = "Genres",
        mediaId = KeyType.GenresRoot.hashCode(),
        isPlayable = false,
        folderType = FOLDER_TYPE_GENRES
    )


    val root: MediaItem = buildMediaItem(
        title = "Root folder",
        mediaId = KeyType.Root.hashCode(),
        isPlayable = false,
        folderType = FOLDER_TYPE_MIXED
    )

    init {
        mediaItemsMap[root.mediaId] = KeyType.Root to root
    }


    private fun getCachedChildren(parent: String): List<MediaItem>? = mediaItemsTree[parent]

    suspend fun getChildren(parent: String): List<MediaItem> {
        val cachedChildren = getCachedChildren(parent = parent)
        if (cachedChildren != null) {
            return cachedChildren
        }
        val parentKey = mediaItemsMap[parent]?.first ?: return listOf()
        val mediaItems: List<Pair<KeyType, MediaItem>> = when (parentKey) {
            is KeyType.Root -> {
                listOf(
                    KeyType.TracksRoot to tracksRoot,
                    KeyType.ArtistsRoot to artistsRoot,
                    KeyType.AlbumsRoot to albumsRoot,
                    KeyType.GenresRoot to genresRoot
                )
            }

            is KeyType.TracksRoot -> {
                trackRepository.getAllTracks().first()
                    .map { KeyType.TrackKey(trackId = it.id) to it.toMediaItem() }
            }

            is KeyType.AlbumsRoot -> {
                albumRepository.getAlbums().first()
                    .map { KeyType.AlbumKey(albumId = it.id) to it.toMediaItem() }
            }

            is KeyType.ArtistsRoot -> {
                artistRepository.getArtists().first()
                    .map { KeyType.ArtistKey(artistId = it.id) to it.toMediaItem() }
            }

            is KeyType.GenresRoot -> {
                genreRepository.getGenres().first()
                    .map { KeyType.GenreKey(genreId = it.id) to it.toMediaItem() }
            }

            is KeyType.PlaylistRoot -> {
                playlistRepository.getPlaylists().first()
                    .map { KeyType.PlaylistKey(playlistId = it.id) to it.toMediaItem() }
            }

            is KeyType.AlbumKey -> {
                trackRepository.getTracksForAlbum(parentKey.albumId).first()
                    .map { KeyType.TrackKey(trackId = it.id) to it.toMediaItem() }
            }

            is KeyType.ArtistKey -> {
                artistRepository.getArtist(parentKey.artistId)
                    .map { it.artistAlbums + it.artistAppearsOn }.first()
                    .map { KeyType.AlbumKey(albumId = it.id) to it.toMediaItem() }
            }

            is KeyType.GenreKey -> {
                trackRepository.getTracksForGenre(parentKey.genreId).first()
                    .map { KeyType.TrackKey(trackId = it.id) to it.toMediaItem() }
            }

            is KeyType.PlaylistKey -> {
                trackRepository.getTracksForPlaylist(parentKey.playlistId).first()
                    .map { KeyType.TrackKey(trackId = it.id) to it.toMediaItem() }
            }

            is KeyType.TrackKey -> throw IllegalStateException("Track should not have children")
        }

        saveItems(mediaItems = mediaItems)
        val items = mediaItems.map { it.second }
        mediaItemsTree[parent] = items
        return items
    }

    private fun saveItems(mediaItems: List<Pair<KeyType, MediaItem>>) {
        mediaItems.forEach { (key, item) ->
            mediaItemsMap[item.mediaId] = key to item
        }
    }

    fun getItem(mediaId: String): MediaItem? {
        return mediaItemsMap[mediaId]?.second
    }

    sealed interface KeyType {
        object Root : KeyType
        object TracksRoot : KeyType
        object AlbumsRoot : KeyType
        object ArtistsRoot : KeyType
        object GenresRoot : KeyType
        object PlaylistRoot : KeyType
        data class AlbumKey(val albumId: Long) : KeyType
        data class ArtistKey(val artistId: Long) : KeyType
        data class GenreKey(val genreId: Long) : KeyType
        data class PlaylistKey(val playlistId: Long) : KeyType
        data class TrackKey(val trackId: Long) : KeyType
    }

//    private suspend fun loadImage(uri: Uri): ByteArray {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            try {
//                val bitmap = loadImageBitmap(uri)
//                Log.i("000Image", "Image loaded not found")
//
//                val stream = ByteArrayOutputStream()
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                stream.toByteArray()
//            } catch (e: Exception) {
//                Log.i("000Image", "Exception loading image: $e")
//                ByteArray(0)
//            }
//        } else {
//            ByteArray(0)
//        }
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    private suspend fun loadImageBitmap(uri: Uri): Bitmap {
//        return withContext(ioDispatcher) {
//            try {
//                Log.i("000Image", "Image loading")
//                context.contentResolver.loadThumbnail(uri, Size(500, 500), null)
//            } catch (e: FileNotFoundException) {
//                Log.i("000Image", "Image loading not found")
//                val d = ContextCompat.getDrawable(context, R.drawable.ic_album)
//                Log.i("000Image", "Image loading drawable done")
//
//                val bitmap = (d as VectorDrawable).toBitmap()
//                Log.i("000Image", "Image loading bitmap done")
//                bitmap
//            }
//        }
//    }
}