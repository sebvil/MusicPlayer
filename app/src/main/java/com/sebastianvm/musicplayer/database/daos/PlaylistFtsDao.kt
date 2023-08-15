package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistFtsDao {
    @Query(
        "SELECT * FROM Playlist " +
            "JOIN PlaylistFts ON Playlist.playlistName == PlaylistFts.playlistName " +
            "WHERE PlaylistFts.playlistName MATCH :text"
    )
    fun playlistsWithText(text: String): Flow<List<Playlist>>
}
