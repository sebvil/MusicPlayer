package com.sebastianvm.musicplayer.database.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.sebastianvm.musicplayer.database.entities.Artist

@Dao
interface ArtistFtsDao {
    @Query("SELECT * FROM Artist JOIN ArtistFts ON Artist.artistId == ArtistFts.artistId WHERE ArtistFts.artistName MATCH :text" )
    fun artistsWithText(text: String): PagingSource<Int, Artist>
}