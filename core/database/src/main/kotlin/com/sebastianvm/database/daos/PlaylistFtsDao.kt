package com.sebastianvm.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.database.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistFtsDao {
    @Query(
        "SELECT * FROM PlaylistEntity " +
            "JOIN PlaylistFts ON PlaylistEntity.playlistName == PlaylistFts.playlistName " +
            "WHERE PlaylistFts.playlistName MATCH :text")
    fun playlistsWithText(text: String): Flow<List<PlaylistEntity>>
}
