package com.sebastianvm.musicplayer.core.database.di

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
import me.tatarka.inject.annotations.Provides

interface DaoProvider {

    @Provides fun getTrackDao(): TrackDao

    @Provides fun getArtistDao(): ArtistDao

    @Provides fun getAlbumDao(): AlbumDao

    @Provides fun getGenreDao(): GenreDao

    @Provides fun getPlaylistDao(): PlaylistDao

    @Provides fun getMediaQueueDao(): MediaQueueDao

    @Provides fun getTrackFtsDao(): TrackFtsDao

    @Provides fun getArtistFtsDao(): ArtistFtsDao

    @Provides fun getAlbumFtsDao(): AlbumFtsDao

    @Provides fun getGenreFtsDao(): GenreFtsDao

    @Provides fun getPlaylistFtsDao(): PlaylistFtsDao
}
