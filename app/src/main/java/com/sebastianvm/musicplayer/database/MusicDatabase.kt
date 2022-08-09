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
import com.sebastianvm.musicplayer.database.entities.AppearsOnForArtist
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
        PlaylistFts::class,
    ],
    views = [TrackWithPlaylistPositionView::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract val trackDao: TrackDao
    abstract val artistDao: ArtistDao
    abstract val albumDao: AlbumDao
    abstract val genreDao: GenreDao
    abstract val playlistDao: PlaylistDao
    abstract val mediaQueueDao: MediaQueueDao

    abstract val trackFtsDao: TrackFtsDao
    abstract val artistFtsDao: ArtistFtsDao
    abstract val albumFtsDao: AlbumFtsDao
    abstract val genreFtsDao: GenreFtsDao
    abstract val playlistFtsDao: PlaylistFtsDao
}
