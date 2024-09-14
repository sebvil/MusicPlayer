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

interface DaoProvider {

    fun getTrackDao(): TrackDao

    fun getArtistDao(): ArtistDao

    fun getAlbumDao(): AlbumDao

    fun getGenreDao(): GenreDao

    fun getPlaylistDao(): PlaylistDao

    fun getMediaQueueDao(): MediaQueueDao

    fun getTrackFtsDao(): TrackFtsDao

    fun getArtistFtsDao(): ArtistFtsDao

    fun getAlbumFtsDao(): AlbumFtsDao

    fun getGenreFtsDao(): GenreFtsDao

    fun getPlaylistFtsDao(): PlaylistFtsDao
}
