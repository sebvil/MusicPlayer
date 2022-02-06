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
        id: String,
        title: String,
        artists: String,
        genres: String,
        albumName: String,
        albumArtists: String,
        year: Long,
        trackNumber: Long,
        numTracks: Long,
        duration: Long,
        albumId: String
    ) {
        val track = Track(
            trackId = id,
            trackName = title,
            trackNumber = trackNumber.toString().substring(1).toLongOrNull() ?: 0L,
            trackDurationMs = duration,
            albumId = albumId
        )
        val trackArtists = parseTag(artists)
        val artistTrackCrossRefs = trackArtists.map { artistName ->
            ArtistTrackCrossRef(
                artistName = artistName,
                trackId = id,
                trackName = title
            )
        }
        val trackArtistsList = trackArtists.map { artistName -> Artist(artistName = artistName) }
        val trackGenres = parseTag(genres).map { genreName -> Genre(genreName = genreName) }
        val genreTrackCrossRef = trackGenres.map { genre ->
            GenreTrackCrossRef(
                genreName = genre.genreName,
                trackId = id
            )
        }
        val albumArtistsList =
            parseTag(albumArtists).map { artistName -> Artist(artistName = artistName) }
        val album = Album(albumId, albumName, year, numTracks)
        val albumForArtists = mutableListOf<AlbumsForArtist>()
        val appearsOnForArtists = mutableListOf<AppearsOnForArtist>()
        trackArtists.forEach { artistName ->
            if (artistName in albumArtistsList.map { artist -> artist.artistName }) {
                albumForArtists.add(
                    AlbumsForArtist(
                        albumId = albumId,
                        artistName = artistName,
                        albumName = albumName
                    )
                )
            } else {
                appearsOnForArtists.add(
                    AppearsOnForArtist(
                        albumId = albumId,
                        artistName = artistName
                    )
                )
            }
        }

        trackSet.add(track)
        artistTrackCrossRefsSet.addAll(artistTrackCrossRefs)
        genreTrackCrossRefsSet.addAll(genreTrackCrossRef)
        artistsSet.addAll(trackArtistsList.plus(albumArtistsList))
        genresSet.addAll(trackGenres)
        albumSet.add(album)
        albumForArtistsSet.addAll(albumForArtists)
        appearsOnForArtistSet.addAll(appearsOnForArtists)

    }

    private fun parseTag(tag: String): List<String> {
        return tag.split("&", ",", "/").map {
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
                    val numTracks = musicCursor.getColumnIndex(MediaStore.Audio.Media.NUM_TRACKS)
                    val duration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                    val relativePathColumn =
                        musicCursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)
                    val fileNameColumn =
                        musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                    //add songs to list
                    var count = 0
                    do {
                        val thisId = musicCursor.getString(idColumn) ?: ""
                        val thisTitle = musicCursor.getString(titleColumn) ?: "No title"
                        val thisArtist = musicCursor.getString(artistColumn) ?: "No artist"
                        val thisAlbum = musicCursor.getString(albumColumn) ?: "No album"
                        val thisAlbumArtists =
                            musicCursor.getString(albumArtistColumn) ?: "No album artists"
                        val thisYear = musicCursor.getLong(year)
                        val thisGenre = musicCursor.getString(genres) ?: "No genre"
                        val thisTrackNumber = musicCursor.getLong(trackNumber)
                        val thisNumTracks = musicCursor.getLong(numTracks)
                        val thisDuration = musicCursor.getLong(duration)
                        val albumId = musicCursor.getString(albumIdColumn)
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
                            thisTitle,
                            thisArtist,
                            thisGenre,
                            thisAlbum,
                            thisAlbumArtists,
                            thisYear,
                            thisTrackNumber,
                            thisNumTracks,
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
