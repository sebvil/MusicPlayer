package com.sebastianvm.musicplayer.repository.music

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.sebastianvm.musicplayer.database.entities.AlbumEntity
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.GenreEntity
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.TrackEntity
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.uri.UriUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class MusicRepositoryImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val trackRepository: TrackRepository,
) : MusicRepository {

    private val trackSet = mutableSetOf<TrackEntity>()
    private val artistTrackCrossRefsSet = mutableSetOf<ArtistTrackCrossRef>()
    private val genreTrackCrossRefsSet = mutableSetOf<GenreTrackCrossRef>()
    private val artistsSet = mutableSetOf<ArtistEntity>()
    private val albumSet = mutableSetOf<AlbumEntity>()
    private val genresSet = mutableSetOf<GenreEntity>()
    private val albumForArtistsSet = mutableSetOf<AlbumsForArtist>()
    private val appearsOnForArtistSet = mutableSetOf<AppearsOnForArtist>()

    private fun insertTrack(
        id: Long,
        path: String,
        title: String,
        artists: String,
        genres: String,
        albumName: String,
        albumArtists: String,
        year: Long,
        trackNumber: Long,
        duration: Long,
        albumId: Long,
    ) {
        val track =
            TrackEntity(
                id = id,
                trackName = title,
                trackNumber = trackNumber.toString().substring(1).toLongOrNull() ?: 0L,
                trackDurationMs = duration,
                albumId = albumId,
                albumName = albumName,
                artists = artists,
                path = path,
            )
        val trackArtists = parseTag(artists)
        val artistTrackCrossRefs =
            trackArtists.map { artistName ->
                ArtistTrackCrossRef(
                    artistId = artistName.hashCode().toLong(),
                    artistName = artistName,
                    trackId = id,
                    trackName = title,
                )
            }
        val trackArtistList =
            artistTrackCrossRefs.map { artistTrackCrossRef ->
                ArtistEntity(
                    id = artistTrackCrossRef.artistId,
                    name = artistTrackCrossRef.artistName,
                )
            }
        val trackGenres =
            parseTag(genres).map { genreName ->
                GenreEntity(id = genreName.hashCode().toLong(), name = genreName)
            }
        val genreTrackCrossRef =
            trackGenres.map { genre -> GenreTrackCrossRef(genreId = genre.id, trackId = id) }
        val albumArtistList =
            parseTag(albumArtists).map { artistName ->
                ArtistEntity(id = artistName.hashCode().toLong(), name = artistName)
            }
        val album =
            AlbumEntity(
                id = albumId,
                title = albumName,
                year = year,
                artists = albumArtistList.joinToString(", ") { it.name },
                imageUri = UriUtils.getAlbumUriString(albumId),
            )
        val albumForArtists = mutableListOf<AlbumsForArtist>()
        val appearsOnForArtists = mutableListOf<AppearsOnForArtist>()
        trackArtistList.forEach { artist ->
            if (artist.name in albumArtistList.map { it.name }) {
                albumForArtists.add(
                    AlbumsForArtist(
                        albumId = albumId,
                        artistId = artist.id,
                        artistName = artist.name,
                        albumName = albumName,
                        year = year,
                    )
                )
            } else {
                appearsOnForArtists.add(
                    AppearsOnForArtist(albumId = albumId, artistId = artist.id, year = year)
                )
            }
        }

        trackSet.add(track)
        artistTrackCrossRefsSet.addAll(artistTrackCrossRefs)
        genreTrackCrossRefsSet.addAll(genreTrackCrossRef)
        artistsSet.addAll(trackArtistList.plus(albumArtistList))
        genresSet.addAll(trackGenres)
        albumSet.add(album)
        albumForArtistsSet.addAll(albumForArtists)
        appearsOnForArtistSet.addAll(appearsOnForArtists)
    }

    private fun parseTag(tag: String): List<String> {
        return tag.split("&", ",", "/").map { it.trim() }
    }

    // TODO makes this work for API 29
    @WorkerThread
    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun getMusic(messageCallback: LibraryScanService.MessageCallback) {
        withContext(ioDispatcher) {
            context.let {
                val musicResolver = context.contentResolver
                val musicUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
                val musicCursor = musicResolver.query(musicUri, null, selection, null, null)

                if (musicCursor != null && musicCursor.moveToFirst()) {
                    // get columns
                    val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                    val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                    val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                    val albumArtistColumn =
                        musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ARTIST)
                    val year = musicCursor.getColumnIndex(MediaStore.Audio.Media.YEAR)
                    val genres = musicCursor.getColumnIndex(MediaStore.Audio.Media.GENRE)
                    val trackNumber = musicCursor.getColumnIndex(MediaStore.Audio.Media.TRACK)
                    val duration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                    val relativePathColumn =
                        musicCursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)
                    val fileNameColumn =
                        musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    val dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                    // add songs to list
                    var count = 0
                    do {
                        val thisId = musicCursor.getLong(idColumn)
                        val filePath = musicCursor.getString(dataColumn)
                        val thisTitle = musicCursor.getString(titleColumn) ?: "No title"
                        val thisArtist = musicCursor.getString(artistColumn) ?: "No artist"
                        val thisAlbum = musicCursor.getString(albumColumn) ?: "No album"
                        val thisAlbumArtists =
                            musicCursor.getString(albumArtistColumn) ?: "No album artists"
                        val thisYear = musicCursor.getLong(year)
                        val thisGenre = musicCursor.getString(genres) ?: "No genre"
                        val thisTrackNumber = musicCursor.getLong(trackNumber)
                        val thisDuration = musicCursor.getLong(duration)
                        val albumId = musicCursor.getLong(albumIdColumn)
                        val relativePath = musicCursor.getString(relativePathColumn)
                        val fileName = musicCursor.getString(fileNameColumn)

                        count++
                        messageCallback.updateProgress(
                            musicCursor.count,
                            count,
                            relativePath + fileName,
                        )

                        insertTrack(
                            thisId,
                            filePath,
                            thisTitle,
                            thisArtist,
                            thisGenre,
                            thisAlbum,
                            thisAlbumArtists,
                            thisYear,
                            thisTrackNumber,
                            thisDuration,
                            albumId,
                        )
                    } while (musicCursor.moveToNext())
                }
                musicCursor?.close()
                trackRepository.insertAllTracks(
                    tracks = trackSet,
                    artistTrackCrossRefs = artistTrackCrossRefsSet,
                    genreTrackCrossRefs = genreTrackCrossRefsSet,
                    artists = artistsSet,
                    genres = genresSet,
                    albums = albumSet,
                    albumsForArtists = albumForArtistsSet,
                    appearsOnForArtists = appearsOnForArtistSet,
                )
            }
            messageCallback.onFinished()
        }
    }
}
