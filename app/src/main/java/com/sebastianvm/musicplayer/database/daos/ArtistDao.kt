package com.sebastianvm.musicplayer.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.flow.Flow


@Dao
interface ArtistDao {

    @Query(
        "SELECT * from Artist ORDER BY " +
                "CASE WHEN :sortOrder='ASCENDING' THEN artistName END COLLATE LOCALIZED ASC, " +
                "CASE WHEN :sortOrder='DESCENDING' THEN artistName END COLLATE LOCALIZED DESC"
    )
    fun getArtists(sortOrder: MediaSortOrder): Flow<List<Artist>>

    @Transaction
    @Query("SELECT * from Artist WHERE Artist.id=:artistId")
    fun getArtist(artistId: Long): Flow<ArtistWithAlbums>

    @Query("SELECT COUNT(*) FROM Artist")
    fun getArtistsCount(): Flow<Int>

    @Query("SELECT * FROM ARTIST WHERE Artist.id IN (:artistsIds)")
    fun getArtists(artistsIds: List<Long>): Flow<List<Artist>>
}
