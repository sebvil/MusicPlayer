package com.sebastianvm.musicplayer.database.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.sebastianvm.musicplayer.model.Album

data class AlbumWithArtists(
    @Embedded val album: AlbumEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        entity = ArtistEntity::class,
        associateBy =
            Junction(AlbumsForArtist::class, parentColumn = "albumId", entityColumn = "artistId"),
    )
    val artists: List<ArtistEntity>,
)

fun AlbumWithArtists.asExternalModel(): Album {
    return Album(
        id = album.id,
        title = album.title,
        imageUri = album.imageUri,
        artists = artists.map { it.asExternalModel() },
        year = album.year,
    )
}
