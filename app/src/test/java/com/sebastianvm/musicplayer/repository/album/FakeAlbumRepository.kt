package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class FakeAlbumRepository(fullAlbumInfo: List<FullAlbumInfo> = listOf()) : AlbumRepository {

    private val albumList = MutableStateFlow(fullAlbumInfo)

    override fun getAlbumsCount(): Flow<Int> = albumList.map { it.size }
    override fun getAlbums(sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>): Flow<List<Album>> {
        return albumList.map { albums ->
            albums.map { it.album }.run {
                val sortedAlbums = when (sortPreferences.sortOption) {
                    SortOptions.AlbumListSortOptions.ALBUM -> sortedBy { it.albumName }
                    SortOptions.AlbumListSortOptions.YEAR -> sortedBy { it.year }
                    SortOptions.AlbumListSortOptions.ARTIST -> sortedBy { it.artists }
                }

                if (sortPreferences.sortOrder == MediaSortOrder.DESCENDING) {
                    sortedAlbums.reversed()
                } else {
                    sortedAlbums
                }
            }
        }
    }


    override fun getAlbums(albumIds: List<Long>): Flow<List<Album>> = albumList.map { albums ->
        albums.mapNotNull { album -> album.takeIf { it.album.albumId in albumIds }?.album }.toList()
    }

    override fun getAlbum(albumId: Long): Flow<FullAlbumInfo> =
        albumList.mapNotNull { albums -> albums.find { it.album.albumId == albumId } }
}
