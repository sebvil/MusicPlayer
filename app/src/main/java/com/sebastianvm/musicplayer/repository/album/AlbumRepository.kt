package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {
    fun getAlbumsCount(): Flow<Int>
    fun getAlbums(sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>): Flow<List<Album>>
    fun getAlbums(albumIds: List<String>): Flow<List<Album>>
    fun getAlbum(albumId: String): Flow<FullAlbumInfo>
}
