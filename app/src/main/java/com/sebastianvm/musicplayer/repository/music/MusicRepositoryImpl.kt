package com.sebastianvm.musicplayer.repository.music

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.sebastianvm.musicplayer.database.MusicDatabase
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.coroutines.IODispatcher
import com.sebastianvm.musicplayer.util.uri.UriUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val musicDatabase: MusicDatabase,
    private val trackRepository: TrackRepository,
    private val artistRepository: ArtistRepository,
    private val genreRepository: GenreRepository,
    private val albumRepository: AlbumRepository,
    private val playlistRepository: PlaylistRepository,
) : MusicRepository {

    private val trackSet = mutableSetOf<Track>()
    private val artistTrackCrossRefsSet = mutableSetOf<ArtistTrackCrossRef>()
    private val genreTrackCrossRefsSet = mutableSetOf<GenreTrackCrossRef>()
    private val artistsSet = mutableSetOf<Artist>()
    private val albumSet = mutableSetOf<Album>()
    private val genresSet = mutableSetOf<Genre>()
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
        albumId: Long
    ) {
        val track = Track(
            id = id,
            trackName = title,
            trackNumber = trackNumber.toString().substring(1).toLongOrNull() ?: 0L,
            trackDurationMs = duration,
            albumId = albumId,
            albumName = albumName,
            artists = artists,
            path = path
        )
        val trackArtists = parseTag(artists)
        val artistTrackCrossRefs = trackArtists.map { artistName ->
            ArtistTrackCrossRef(
                artistId = artistName.hashCode().toLong(),
                artistName = artistName,
                trackId = id,
                trackName = title
            )
        }
        val trackArtistList = artistTrackCrossRefs.map { artistTrackCrossRef ->
            Artist(
                id = artistTrackCrossRef.artistId,
                artistName = artistTrackCrossRef.artistName
            )
        }
        val trackGenres = parseTag(genres).map { genreName ->
            Genre(
                id = genreName.hashCode().toLong(),
                genreName = genreName
            )
        }
        val genreTrackCrossRef = trackGenres.map { genre ->
            GenreTrackCrossRef(
                genreId = genre.id,
                trackId = id
            )
        }
        val albumArtistList =
            parseTag(albumArtists).map { artistName ->
                Artist(
                    id = artistName.hashCode().toLong(),
                    artistName = artistName
                )
            }
        val album = Album(
            id = albumId,
            albumName = albumName,
            year = year,
            artists = albumArtistList.joinToString(", ") { it.artistName },
            imageUri = UriUtils.getAlbumUriString(albumId)
        )
        val albumForArtists = mutableListOf<AlbumsForArtist>()
        val appearsOnForArtists = mutableListOf<AppearsOnForArtist>()
        trackArtistList.forEach { artist ->
            if (artist.artistName in albumArtistList.map { it.artistName }) {
                albumForArtists.add(
                    AlbumsForArtist(
                        albumId = albumId,
                        artistId = artist.id,
                        artistName = artist.artistName,
                        albumName = albumName,
                        year = year
                    )
                )
            } else {
                appearsOnForArtists.add(
                    AppearsOnForArtist(
                        albumId = albumId,
                        artistId = artist.id,
                        year = year
                    )
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
        return tag.split("&", ";", "/", ",").map {
            it.trim()
        }
    }

    override fun getCounts(): Flow<CountHolder> {
        return combine(
            trackRepository.getTracksCount(),
            artistRepository.getArtistsCount(),
            albumRepository.getAlbumsCount(),
            genreRepository.getGenresCount(),
            playlistRepository.getPlaylistsCount()
        ) { tracksCount, artistsCount, albumCounts, genreCounts, playlistCounts ->
            CountHolder(tracksCount, artistsCount, albumCounts, genreCounts, playlistCounts)
        }.distinctUntilChanged()
    }


    // TODO makes this work for API 29
    @WorkerThread
    @RequiresApi(Build.VERSION_CODES.R)
    override suspend fun getMusic(messageCallback: LibraryScanService.MessageCallback) {
        withContext(ioDispatcher) {
            musicDatabase.clearAllTables()
            context.let {
                val musicResolver = context.contentResolver
                val musicUri =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Audio.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL_PRIMARY
                        )
                    } else {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }

                val selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1"
                val musicCursor = musicResolver.query(musicUri, null, selection, null, null)

                if (musicCursor != null && musicCursor.moveToFirst()) {
                    //get columns
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
                    //add songs to list
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
                            relativePath + fileName
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
                            albumId
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
