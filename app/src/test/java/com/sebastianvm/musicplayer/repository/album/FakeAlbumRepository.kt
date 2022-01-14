package com.sebastianvm.musicplayer.repository.album

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.FullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAlbumRepository : AlbumRepository {

    var albumCount = 2L

    override fun getAlbumsCount(): Flow<Long> {
        return flow {
            emit(albumCount)
        }
    }

    override fun getAlbums(): Flow<List<AlbumWithArtists>> {
        TODO("Not yet implemented")
    }

    override fun getAlbums(albumIds: List<String>): Flow<List<AlbumWithArtists>> {
        TODO("Not yet implemented")
    }

    override fun getAlbum(albumId: String): Flow<FullAlbumInfo> {
        TODO("Not yet implemented")
    }

    override fun getAlbumWithTracks(albumId: String): Flow<Map<Album, List<FullTrackInfo>>> {
        TODO("Not yet implemented")
    }


}
