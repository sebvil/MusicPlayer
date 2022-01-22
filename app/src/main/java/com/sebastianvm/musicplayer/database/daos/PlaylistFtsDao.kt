package com.sebastianvm.musicplayer.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Playlist

@Dao
interface PlaylistFtsDao {
    @Query("SELECT * FROM Playlist " +
            "JOIN PlaylistFts ON Playlist.playlistName == PlaylistFts.playlistName " +
            "WHERE PlaylistFts.playlistName MATCH :text" )
    fun playlistsWithText(text: String): PagingSource<Int, Playlist>
}
