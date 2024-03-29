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
import com.sebastianvm.musicplayer.database.daos.PlaylistDao
import com.sebastianvm.musicplayer.database.daos.PlaylistFtsDao
import com.sebastianvm.musicplayer.database.daos.TrackDao
import com.sebastianvm.musicplayer.database.daos.TrackFtsDao
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumFts
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.AlbumsForArtistByYear
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtistByYear
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistFts
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.GenreFts
import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.MediaQueueItem
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.database.entities.PlaylistFts
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackFts
import com.sebastianvm.musicplayer.database.entities.TrackWithPlaylistPositionView

@Database(
    entities = [
        Track::class,
        Artist::class,
        Album::class,
        Genre::class,
        Playlist::class,
        MediaQueueItem::class,
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
        PlaylistFts::class
    ],
    views = [TrackWithPlaylistPositionView::class, AlbumsForArtistByYear::class, AppearsOnForArtistByYear::class],
    version = 1,
    exportSchema = false
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
