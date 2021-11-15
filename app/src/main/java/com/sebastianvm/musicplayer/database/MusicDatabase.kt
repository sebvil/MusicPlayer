package com.sebastianvm.musicplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.entities.*

@Database(
    entities = [
        Track::class,
        Artist::class,
        Album::class,
        Genre::class,
        ArtistTrackCrossRef::class,
        GenreTrackCrossRef::class,
        AlbumsForArtist::class,
        AppearsOnForArtist::class,
    ],
    version = 12,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract val trackDao: TrackDao
    abstract val artistDao: ArtistDao
    abstract val albumDao: AlbumDao
    abstract val genreDao: GenreDao

}