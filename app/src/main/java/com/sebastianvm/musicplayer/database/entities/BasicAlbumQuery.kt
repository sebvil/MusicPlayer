package com.sebastianvm.musicplayer.database.entities

import com.sebastianvm.musicplayer.model.BasicAlbum

data class BasicAlbumQuery(val id: Long, val title: String, val imageUri: String)

fun BasicAlbumQuery.asExternalModel(): BasicAlbum {
    return BasicAlbum(id = id, title = title, imageUri = imageUri)
}
