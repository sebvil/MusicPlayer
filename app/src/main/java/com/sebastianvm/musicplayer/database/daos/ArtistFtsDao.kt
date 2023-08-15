package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistFtsDao {
    @Query(
        "SELECT * FROM Artist " +
            "JOIN ArtistFts ON Artist.artistName == ArtistFts.artistName " +
            "WHERE ArtistFts.artistName MATCH :text"
    )
    fun artistsWithText(text: String): Flow<List<Artist>>
}
