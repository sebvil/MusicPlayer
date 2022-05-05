package com.sebastianvm.musicplayer.repository.music

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File
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
        path: String,
        title: String,
        artists: String,
        genres: String,
        albumName: String,
        albumArtists: String,
        year: Long,
        trackNumber: Long,
        duration: Long,
        albumId: String
    ) {
        val track = Track(
            trackId = id,
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
        val trackArtistsList = trackArtists.map { artistName ->
            Artist(
                artistId = artistName.hashCode().toLong(),
                artistName = artistName
            )
        }
        val trackGenres = parseTag(genres).map { genreName -> Genre(genreName = genreName) }
        val genreTrackCrossRef = trackGenres.map { genre ->
            GenreTrackCrossRef(
                genreName = genre.genreName,
                trackId = id
            )
        }
        val albumArtistsList =
            parseTag(albumArtists).map { artistName -> Artist(artistName = artistName) }
        val album = Album(
            albumId = albumId,
            albumName = albumName,
            year = year,
            artists = albumArtistsList.joinToString(", ") { it.artistName })
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

    @Suppress("BlockingMethodInNonBlockingContext")
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
                    val totalCount = musicCursor.count
                    //get columns
                    val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                    val dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                    val durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                    val albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                    val trackNumberColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TRACK)


                    //add songs to list
                    var count = 0
                    val jobs = mutableListOf<Job>()
                    do {
                        val id = musicCursor.getString(idColumn)
                        val filePath = musicCursor.getString(dataColumn)
                        val duration = musicCursor.getLong(durationColumn)
                        val albumId = musicCursor.getString(albumIdColumn)
                        val trackNumber = musicCursor.getLong(trackNumberColumn)

                        val job = launch {
                            val file = File(filePath)
                            try {
                                val f = AudioFileIO.read(file)
                                val tag: Tag = f.tag
                                insertTrack(
                                    id = id,
                                    path = filePath,
                                    title = tag.getFirst(FieldKey.TITLE),
                                    artists = tag.getFirst(FieldKey.ARTISTS),
                                    genres = tag.getFirst(FieldKey.GENRE),
                                    albumName = tag.getFirst(FieldKey.ALBUM),
                                    albumArtists = tag.getFirst(FieldKey.ALBUM_ARTIST),
                                    year = tag.getFirst(FieldKey.YEAR).toLongOrNull() ?: 0,
                                    trackNumber = trackNumber,
                                    duration = duration,
                                    albumId = albumId
                                )
                            } catch (e: Exception) {
                                Log.e("FILE", "$filePath, $e")
                            }

                            count++
                            messageCallback.updateProgress(
                                progressMax = totalCount,
                                currentProgress = count,
                                filePath = filePath
                            )
                        }

                        jobs.add(job)

                    } while (musicCursor.moveToNext())
                    jobs.joinAll()
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
