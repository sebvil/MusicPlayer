package com.sebastianvm.musicplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sebastianvm.musicplayer.database.daos.AlbumDao
import com.sebastianvm.musicplayer.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.database.daos.ArtistDao
import com.sebastianvm.musicplayer.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.database.daos.GenreDao
import com.sebastianvm.musicplayer.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumFts
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistFts
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreFts
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
        ArtistFts::class,
        AlbumFts::class,
        GenreFts::class,
    ],
    version = 20,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract val trackDao: TrackDao
    abstract val artistDao: ArtistDao
    abstract val albumDao: AlbumDao
    abstract val genreDao: GenreDao
    abstract val mediaQueueDao: MediaQueueDao

    abstract val trackFtsDao: TrackFtsDao
    abstract val artistFtsDao: ArtistFtsDao
    abstract val albumFtsDao: AlbumFtsDao
    abstract val genreFtsDao: GenreFtsDao
}