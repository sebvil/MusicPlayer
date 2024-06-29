package com.sebastianvm.musicplayer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sebastianvm.musicplayer.core.database.daos.AlbumDao
import com.sebastianvm.musicplayer.core.database.daos.AlbumFtsDao
import com.sebastianvm.musicplayer.core.database.daos.ArtistDao
import com.sebastianvm.musicplayer.core.database.daos.ArtistFtsDao
import com.sebastianvm.musicplayer.core.database.daos.GenreDao
import com.sebastianvm.musicplayer.core.database.daos.GenreFtsDao
import com.sebastianvm.musicplayer.core.database.daos.MediaQueueDao
import com.sebastianvm.musicplayer.core.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.core.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.core.database.daos.TrackDao
import com.sebastianvm.musicplayer.core.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.core.database.entities.AlbumFts
import com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtistByYear
import com.sebastianvm.musicplayer.core.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.core.database.entities.AppearsOnForArtistByYear
import com.sebastianvm.musicplayer.core.database.entities.ArtistEntity
import com.sebastianvm.musicplayer.core.database.entities.ArtistFts
import com.sebastianvm.musicplayer.core.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.GenreEntity
import com.sebastianvm.musicplayer.core.database.entities.GenreFts
import com.sebastianvm.musicplayer.core.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.PlaylistEntity
import com.sebastianvm.musicplayer.core.database.entities.PlaylistFts
import com.sebastianvm.musicplayer.core.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.core.database.entities.QueueItemEntity
import com.sebastianvm.musicplayer.core.database.entities.QueueItemWithTrack
import com.sebastianvm.musicplayer.core.database.entities.TrackEntity
import com.sebastianvm.musicplayer.core.database.entities.TrackFts
import com.sebastianvm.musicplayer.core.database.entities.TrackWithPlaylistPositionView

@Database(
    entities =
        [
            TrackEntity::class,
            ArtistEntity::class,
            com.sebastianvm.musicplayer.core.database.entities.AlbumEntity::class,
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
