package com.sebastianvm.musicplayer.core.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.core.database.entities.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistFtsDao {
    @Query(
        "SELECT * FROM ArtistEntity " +
            "JOIN ArtistFts ON ArtistEntity.name == ArtistFts.name " +
            "WHERE ArtistFts.name MATCH :text"
    )
    fun artistsWithText(text: String): Flow<List<ArtistEntity>>
}
