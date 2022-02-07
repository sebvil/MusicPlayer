package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    // TODO support playlists - migrate to Proto
    suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String,
    )

    fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings>

    suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings)
    fun getAlbumsListSortOptions(): Flow<MediaSortSettings>

    suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getArtistsListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getGenresListSortOrder(): Flow<MediaSortOrder>

    suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder)
    fun getPlaylistsListSortOrder(): Flow<MediaSortOrder>


    suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo)

    fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo>
}
