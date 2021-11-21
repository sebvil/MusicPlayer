package com.sebastianvm.musicplayer.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.sebastianvm.musicplayer.database.MusicDatabase
import com.sebastianvm.musicplayer.database.entities.*
import com.sebastianvm.musicplayer.ui.util.mvvm.NonNullMediatorLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val musicDatabase: MusicDatabase,
    private val trackRepository: TrackRepository,
    private val artistRepository: ArtistRepository,
    private val genreRepository: GenreRepository,
    private val albumRepository: AlbumRepository,
) {

    private val trackSet = mutableSetOf<Track>()
    private val artistTrackCrossRefsSet  = mutableSetOf<ArtistTrackCrossRef>()
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
        albumGid: String
    ) {
        val track = Track(
            id,
            title,
            trackNumber.toString().substring(1).toLongOrNull() ?: 0L,
            duration,
            albumGid
        )
        val trackArtists = parseTag(artists)
        val artistTrackCrossRefs = trackArtists.map { ArtistTrackCrossRef(it, id) }
        val trackArtistsList = trackArtists.map { Artist(it, it) }
        val trackGenres = parseTag(genres).map { Genre(it) }
        val genreTrackCrossRef = trackGenres.map { GenreTrackCrossRef(it.genreName, id) }
        val albumArtistsList = parseTag(albumArtists).map { Artist(it, it) }
        val album = Album(albumGid, albumName, year, "", numTracks)
        val albumForArtists = mutableListOf<AlbumsForArtist>()
        val appearsOnForArtists = mutableListOf<AppearsOnForArtist>()
        trackArtists.forEach { artistName ->
            if (artistName in albumArtistsList.map { it.artistGid }) {
                albumForArtists.add(AlbumsForArtist(albumGid, artistName))
            } else {
                appearsOnForArtists.add(AppearsOnForArtist(albumGid, artistName))
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

    data class CountHolder(
        val tracks: Long,
        val artists: Long,
        val albums: Long,
        val genres: Long,
    )

    fun getCounts(): LiveData<CountHolder> {
        val countsMediatorLiveData = NonNullMediatorLiveData(CountHolder(0, 0, 0, 0))

        countsMediatorLiveData.addSource(trackRepository.getTracksCount()) {
            val value = countsMediatorLiveData.value.copy(tracks = it)
            countsMediatorLiveData.value = value
        }

        countsMediatorLiveData.addSource(artistRepository.getArtistsCount()) {
            val value = countsMediatorLiveData.value.copy(artists = it)
            countsMediatorLiveData.value = value
        }

        countsMediatorLiveData.addSource(albumRepository.getAlbumsCount()) {
            val value = countsMediatorLiveData.value.copy(albums = it)
            countsMediatorLiveData.value = value
        }

        countsMediatorLiveData.addSource(genreRepository.getGenresCount()) {
            val value = countsMediatorLiveData.value.copy(genres = it)
            countsMediatorLiveData.value = value
        }

        return countsMediatorLiveData
    }


    // TODO makes this work for API 29
    @WorkerThread
    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun getMusic(messageCallback: LibraryScanService.MessageCallback) {

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
                val fileNameColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
                //add songs to list
                var count = 0
                do {
                    val thisId = musicCursor.getLong(idColumn)
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
                        thisId.toString(),
                        thisTitle,
                        thisArtist,
                        thisGenre,
                        thisAlbum,
                        thisAlbumArtists,
                        thisYear,
                        thisTrackNumber,
                        thisNumTracks,
                        thisDuration,
                        albumId.toString()
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