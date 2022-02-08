package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import kotlinx.coroutines.flow.Flow

class FakePreferencesRepository :
    PreferencesRepository {
    override suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String
    ) {
        TODO("Not yet implemented")
    }

    override fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        TODO("Not yet implemented")
    }

    override fun getAlbumsListSortOptions(): Flow<MediaSortSettings> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        TODO("Not yet implemented")
    }

    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        TODO("Not yet implemented")
    }

    override fun getGenresListSortOrder(): Flow<MediaSortOrder> {
        TODO("Not yet implemented")
    }

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        TODO("Not yet implemented")
    }

    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> {
        TODO("Not yet implemented")
    }

    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) {
        TODO("Not yet implemented")
    }

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        TODO("Not yet implemented")
    }


}
