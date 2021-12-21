package com.sebastianvm.musicplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackFts

@Database(
    entities = [
        Track::class,
        Artist::class,
        Album::class,
        Genre::class,
        MediaQueue::class,
        ArtistTrackCrossRef::class,
        GenreTrackCrossRef::class,
        AlbumsForArtist::class,
        AppearsOnForArtist::class,
        MediaQueueTrackCrossRef::class,
        TrackFts::class,
    ],
    version = 14,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract val trackDao: TrackDao
    abstract val artistDao: ArtistDao
    abstract val albumDao: AlbumDao
    abstract val genreDao: GenreDao
    abstract val trackFtsDao: TrackFtsDao
}