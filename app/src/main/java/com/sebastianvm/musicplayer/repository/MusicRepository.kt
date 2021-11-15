package com.sebastianvm.musicplayer.repository

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.sebastianvm.musicplayer.database.MusicDatabase
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.util.PreferencesUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val musicDatabase: MusicDatabase,
    private val preferencesUtil: PreferencesUtil,
    private val trackRepository: TrackRepository,
    private val artistRepository: ArtistRepository,
    private val genreRepository: GenreRepository,
    private val albumRepository: AlbumRepository,
) {
    private suspend fun insertTrack(
        id: String,
        title: String,
        artists: String,
        genres: String,
        albumName: String,
        albumArtists: String,
        year: Long,
        albumArtPath: String,
        trackNumber: Long,
        numTracks: Long,
        duration: Long,
        albumGid: String
    ) {
        val trackArtists = parseTag(artists)
        artistRepository.insertArtists(trackArtists, trackArtists)
        val trackGenres = parseTag(genres)
        genreRepository.insertGenres(trackGenres)
        albumRepository.insertAlbum(Album(albumGid, albumName, year, albumArtPath, numTracks))
        trackRepository.insertTrack(
            Track(
                id,
                title,
                trackNumber.toString().substring(1).toLongOrNull() ?: 0L,
                duration,
                albumGid
            ),
            trackArtists,
            trackGenres,
        )
        val albumArtistsList = parseTag(albumArtists)
        artistRepository.insertArtists(albumArtistsList, albumArtistsList)
        val albumForArtists = mutableListOf<String>()
        val appearsOnForArtists = mutableListOf<String>()
        trackArtists.forEach { artistName ->
            if (artistName in albumArtistsList) {
                albumForArtists.add(artistName)
            } else {
                appearsOnForArtists.add(artistName)
            }
        }
        albumRepository.insertAlbumForArtists(albumGid, albumForArtists)
        albumRepository.insertAppearsOnForArtists(albumGid, appearsOnForArtists)
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
        val counts: Flow<CountHolder> = preferencesUtil.dataStore.data
            .map { preferences ->
                val trackCounts = preferences[PreferencesUtil.TRACK_COUNT] ?: -1
                val artistCounts = preferences[PreferencesUtil.ARTIST_COUNT] ?: -1
                val albumCounts = preferences[PreferencesUtil.ALBUM_COUNT] ?: -1
                val genreCounts = preferences[PreferencesUtil.GENRE_COUNT] ?: -1
                if (trackCounts == -1L || artistCounts == -1L || albumCounts == -1L || genreCounts == -1L) {
                    updateCounts()
                }
                CountHolder(trackCounts, artistCounts, albumCounts, genreCounts)
            }
        return counts.asLiveData()
    }

    suspend fun updateCounts() {
        preferencesUtil.dataStore.edit { settings ->
            settings[PreferencesUtil.TRACK_COUNT] = trackRepository.getTracksCount()
            settings[PreferencesUtil.ARTIST_COUNT] = artistRepository.getArtistsCount()
            settings[PreferencesUtil.ALBUM_COUNT] = albumRepository.getAlbumsCount()
            settings[PreferencesUtil.GENRE_COUNT] = genreRepository.getGenresCount()
        }
    }


    // TODO makes this work for API 29
    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun getMusic() {

        withContext(Dispatchers.IO) {
            musicDatabase.clearAllTables()
        }


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
                //add songs to list
                do {
                    val thisId = musicCursor.getString(idColumn)
                    val thisTitle = musicCursor.getString(titleColumn)
                    val thisArtist = musicCursor.getString(artistColumn)
                    val thisAlbum = musicCursor.getString(albumColumn)
                    val thisAlbumArtists = musicCursor.getString(albumArtistColumn) ?: ""
                    val thisYear = musicCursor.getLong(year)
                    val thisGenre = musicCursor.getString(genres)
                    val thisTrackNumber = musicCursor.getLong(trackNumber)
                    val thisNumTracks = musicCursor.getLong(numTracks)
                    val thisDuration = musicCursor.getLong(duration)

                    val albumId = musicCursor.getString(albumIdColumn)
                    insertTrack(
                        thisId,
                        thisTitle,
                        thisArtist,
                        thisGenre,
                        thisAlbum,
                        thisAlbumArtists,
                        thisYear,
                        "",
                        thisTrackNumber,
                        thisNumTracks,
                        thisDuration,
                        albumId
                    )
                } while (musicCursor.moveToNext())
            }
            musicCursor?.close()
        }
        updateCounts()
    }

}