package com.sebastianvm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sebastianvm.database.daos.AlbumDao
import com.sebastianvm.database.daos.AlbumFtsDao
import com.sebastianvm.database.daos.ArtistDao
import com.sebastianvm.database.daos.ArtistFtsDao
import com.sebastianvm.database.daos.GenreDao
import com.sebastianvm.database.daos.GenreFtsDao
import com.sebastianvm.database.daos.MediaQueueDao
import com.sebastianvm.database.daos.PlaylistDao
import com.sebastianvm.database.daos.PlaylistFtsDao
import com.sebastianvm.database.daos.TrackDao
import com.sebastianvm.database.daos.TrackFtsDao
import com.sebastianvm.database.entities.AlbumEntity
import com.sebastianvm.database.entities.AlbumFts
import com.sebastianvm.database.entities.AlbumsForArtist
import com.sebastianvm.database.entities.AlbumsForArtistByYear
import com.sebastianvm.database.entities.AppearsOnForArtist
import com.sebastianvm.database.entities.AppearsOnForArtistByYear
import com.sebastianvm.database.entities.ArtistEntity
import com.sebastianvm.database.entities.ArtistFts
import com.sebastianvm.database.entities.ArtistTrackCrossRef
import com.sebastianvm.database.entities.GenreEntity
import com.sebastianvm.database.entities.GenreFts
import com.sebastianvm.database.entities.GenreTrackCrossRef
import com.sebastianvm.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.database.entities.PlaylistEntity
import com.sebastianvm.database.entities.PlaylistFts
import com.sebastianvm.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.database.entities.QueueItemEntity
import com.sebastianvm.database.entities.QueueItemWithTrack
import com.sebastianvm.database.entities.TrackEntity
import com.sebastianvm.database.entities.TrackFts
import com.sebastianvm.database.entities.TrackWithPlaylistPositionView

@Database(
    entities =
        [
            TrackEntity::class,
            ArtistEntity::class,
            AlbumEntity::class,
            GenreEntity::class,
            PlaylistEntity::class,
            QueueItemEntity::class,
            ArtistTrackCrossRef::class,
            GenreTrackCrossRef::class,
            AlbumsForArtist::class,
            AppearsOnForArtist::class,
            MediaQueueTrackCrossRef::class,
            PlaylistTrackCrossRef::class,
            TrackFts::class,
            ArtistFts::class,
            AlbumFts::class,
            GenreFts::class,
            PlaylistFts::class,
        ],
    views =
        [
            TrackWithPlaylistPositionView::class,
            AlbumsForArtistByYear::class,
            AppearsOnForArtistByYear::class,
            QueueItemWithTrack::class,
        ],
    version = 1,
    exportSchema = false,
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getTrackDao(): TrackDao

    abstract fun getArtistDao(): ArtistDao

    abstract fun getAlbumDao(): AlbumDao

    abstract fun getGenreDao(): GenreDao

    abstract fun getPlaylistDao(): PlaylistDao

    abstract fun getMediaQueueDao(): MediaQueueDao

    abstract fun getTrackFtsDao(): TrackFtsDao

    abstract fun getArtistFtsDao(): ArtistFtsDao

    abstract fun getAlbumFtsDao(): AlbumFtsDao

    abstract fun getGenreFtsDao(): GenreFtsDao

    abstract fun getPlaylistFtsDao(): PlaylistFtsDao
}
