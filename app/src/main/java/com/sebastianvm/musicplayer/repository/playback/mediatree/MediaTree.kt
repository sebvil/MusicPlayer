package com.sebastianvm.musicplayer.repository.playback.mediatree

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_FOLDER_ARTISTS
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
import com.sebastianvm.model.AlbumWithArtists
import com.sebastianvm.model.BasicArtist
import com.sebastianvm.model.Track
import com.sebastianvm.musicplayer.ArtworkProvider
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.uri.UriUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MediaTree(
    private val artistRepository: ArtistRepository,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
) {

    private val mediaItemsTree: MutableMap<String, List<MediaItem>> = mutableMapOf()
    private val mediaItemsMap: MutableMap<String, MediaItem> = mutableMapOf()

    private val rootKey =
        MediaKey(parentType = KeyType.UNKNOWN, parentId = 0, type = KeyType.ROOT, itemIndexOrId = 0)

    // TODO unify builders
    private fun buildMediaItem(
        title: String,
        mediaId: MediaKey,
        isPlayable: Boolean,
        mediaType: Int? = null,
        isBrowsable: Boolean,
        subtitle: String? = null,
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        artworkUri: Uri? = null,
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setSubtitle(subtitle)
                .setArtist(artist)
                .setGenre(genre)
                .setIsBrowsable(isBrowsable)
                .setMediaType(mediaType)
                .setIsPlayable(isPlayable)
                .setArtworkUri(artworkUri)
                .build()
        return MediaItem.Builder()
            .setMediaId(mediaId.toString())
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    private fun Track.buildMediaItem(parent: MediaKey, index: Long): MediaItem {
        return buildMediaItem(
            title = name,
            mediaId =
                MediaKey.fromParent(
                    parent = parent,
                    keyType = KeyType.TRACK,
                    itemIndexOrId = index,
                ),
            isPlayable = true,
            mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
            isBrowsable = false,
            album = "",
            subtitle = artists.joinToString { it.name },
            artist = artists.joinToString { it.name },
            genre = "",
            sourceUri = UriUtils.getTrackUri(trackId = id),
            artworkUri = ArtworkProvider.getUriForTrack(albumId),
        )
    }

    private fun AlbumWithArtists.buildMediaItem(parent: MediaKey): MediaItem {
        return buildMediaItem(
            title = title,
            mediaId =
                MediaKey.fromParent(parent = parent, keyType = KeyType.ALBUM, itemIndexOrId = id),
            isPlayable = false,
            mediaType = MediaMetadata.MEDIA_TYPE_ALBUM,
            isBrowsable = true,
            subtitle = artists.joinToString { it.name },
            album = title,
            artist = artists.joinToString { it.name },
            genre = null,
            sourceUri = UriUtils.getAlbumUri(albumId = id),
            artworkUri = ArtworkProvider.getUriForAlbum(id),
        )
    }

    private fun BasicArtist.buildMediaItem(parent: MediaKey): MediaItem {
        return buildMediaItem(
            title = name,
            mediaId =
                MediaKey.fromParent(parent = parent, keyType = KeyType.ARTIST, itemIndexOrId = id),
            isPlayable = false,
            mediaType = MediaMetadata.MEDIA_TYPE_ARTIST,
            isBrowsable = true,
            subtitle = null,
            album = null,
            artist = name,
            genre = null,
            sourceUri = null,
        )
    }

    fun getRoot(): MediaItem {
        return buildMediaItem(
            title = "Root folder",
            mediaId = rootKey,
            isPlayable = false,
            mediaType = MEDIA_TYPE_FOLDER_MIXED,
            isBrowsable = true,
        )
    }

    fun getCachedChildren(parent: String): List<MediaItem>? = mediaItemsTree[parent]

    fun getCachedMediaItem(mediaId: String): MediaItem? = mediaItemsMap[mediaId]

    suspend fun getChildren(parent: String): List<MediaItem>? {
        val parentKey = MediaKey.fromString(parent)
        val mediaItems =
            when (parentKey.type) {
                KeyType.UNKNOWN -> null
                KeyType.ROOT ->
                    listOf(
                        buildMediaItem(
                            title = "All tracks",
                            mediaId =
                                MediaKey.fromParent(
                                    parent = parentKey,
                                    keyType = KeyType.ALL_TRACKS,
                                    itemIndexOrId = 0,
                                ),
                            isPlayable = false,
                            isBrowsable = true,
                        ),
                        buildMediaItem(
                            title = "Albums",
                            mediaId =
                                MediaKey.fromParent(
                                    parent = parentKey,
                                    keyType = KeyType.ALBUMS_ROOT,
                                    itemIndexOrId = 0,
                                ),
                            isPlayable = false,
                            mediaType = MEDIA_TYPE_FOLDER_ALBUMS,
                            isBrowsable = true,
                        ),
                        buildMediaItem(
                            title = "Artists",
                            mediaId =
                                MediaKey.fromParent(
                                    parent = parentKey,
                                    keyType = KeyType.ARTISTS_ROOT,
                                    itemIndexOrId = 0,
                                ),
                            isPlayable = false,
                            mediaType = MEDIA_TYPE_FOLDER_ARTISTS,
                            isBrowsable = true,
                        ),
                    )
                KeyType.ALL_TRACKS -> {
                    trackRepository.getTracksForMedia(MediaGroup.AllTracks).first().mapIndexed {
                        index,
                        track ->
                        track.buildMediaItem(parent = parentKey, index = index.toLong())
                    }
                }
                KeyType.ALBUMS_ROOT -> {
                    albumRepository.getAlbums().first().map { it.buildMediaItem(parentKey) }
                }
                KeyType.ARTISTS_ROOT -> {
                    artistRepository.getArtists().first().map { it.buildMediaItem(parentKey) }
                }
                KeyType.GENRES_ROOT -> null
                KeyType.PLAYLISTS_ROOT -> null
                KeyType.ALBUM -> {
                    trackRepository
                        .getTracksForMedia(MediaGroup.Album(parentKey.itemIndexOrId))
                        .first()
                        .mapIndexed { index, track ->
                            track.buildMediaItem(parent = parentKey, index = index.toLong())
                        }
                }
                KeyType.ARTIST -> {
                    artistRepository
                        .getArtist(parentKey.itemIndexOrId)
                        .map { it.albums }
                        .first()
                        .map { it.buildMediaItem(parentKey) }
                }
                KeyType.GENRE -> null
                KeyType.PLAYLIST -> null
                KeyType.TRACK -> null
            }
        return mediaItems?.also { mediaItemsTree[parent] = it }
    }

    suspend fun getItem(mediaId: String): MediaItem? {
        val mediaKey = MediaKey.fromString(mediaId)
        val mediaItem =
            when (mediaKey.type) {
                KeyType.TRACK -> {
                    trackRepository
                        .getTrack(mediaKey.itemIndexOrId)
                        .first()
                        .buildMediaItem(parent = rootKey, index = 0)
                }
                else -> null
            }
        return mediaItem?.also { mediaItemsMap[mediaId] = it }
    }

    enum class KeyType {
        UNKNOWN,
        ROOT,
        ALL_TRACKS,
        ALBUMS_ROOT,
        ARTISTS_ROOT,
        GENRES_ROOT,
        PLAYLISTS_ROOT,
        ALBUM,
        ARTIST,
        GENRE,
        PLAYLIST,
        TRACK
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
    //                val d = ContextCompat.getDrawable(context, RDrawable.ic_album)
    //                Log.i("000Image", "Image loading drawable done")
    //
    //                val bitmap = (d as VectorDrawable).toBitmap()
    //                Log.i("000Image", "Image loading bitmap done")
    //                bitmap
    //            }
    //        }
    //    }
}
