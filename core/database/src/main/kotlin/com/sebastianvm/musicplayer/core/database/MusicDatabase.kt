package com.sebastianvm.musicplayer.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sebastianvm.musicplayer.core.database.di.DaoProvider
import com.sebastianvm.musicplayer.core.database.entities.AlbumEntity
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
import kotlinx.coroutines.CoroutineDispatcher

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
internal abstract class MusicDatabase : RoomDatabase(), DaoProvider

private var database: DaoProvider? = null

fun getDaoProvider(context: Context, ioDispatcher: CoroutineDispatcher): DaoProvider {
    return database
        ?: Room.databaseBuilder(context, MusicDatabase::class.java, "music_database")
            .setQueryCoroutineContext(context = ioDispatcher)
            .build()
            .also { database = it }
}
