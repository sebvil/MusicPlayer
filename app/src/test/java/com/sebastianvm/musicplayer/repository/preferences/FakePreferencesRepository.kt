package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakePreferencesRepository(trackSortOption: SortOption = SortOption.TRACK_NAME) :
    PreferencesRepository {

    private val albumSortSettings =
        MutableStateFlow(SortSettings(SortOption.ALBUM_NAME, SortOrder.ASCENDING))
    private val trackSortSettings =
        MutableStateFlow(SortSettings(trackSortOption, SortOrder.ASCENDING))
    private val artistSortOrder = MutableStateFlow(SortOrder.ASCENDING)
    private val genresSortOrder = MutableStateFlow(SortOrder.ASCENDING)
    private val playlistSortOrder = MutableStateFlow(SortOrder.ASCENDING)


    override suspend fun modifyTrackListSortOptions(
        sortSettings: SortSettings,
        genreName: String?
    ) = trackSortSettings.emit(sortSettings)

    override fun getTracksListSortOptions(genreName: String?): Flow<SortSettings> =
        trackSortSettings

    override suspend fun modifyAlbumsListSortOptions(sortSettings: SortSettings) =
        albumSortSettings.emit(sortSettings)

    override fun getAlbumsListSortOptions(): Flow<SortSettings> = albumSortSettings


    override suspend fun modifyArtistsListSortOrder(sortOrder: SortOrder) {
        artistSortOrder.emit(!artistSortOrder.value)
    }

    override fun getArtistsListSortOrder(): Flow<SortOrder> = artistSortOrder

    override suspend fun modifyGenresListSortOrder(sortOrder: SortOrder) =
        genresSortOrder.emit(sortOrder)

    override fun getGenresListSortOrder(): Flow<SortOrder> = genresSortOrder
    override suspend fun modifyPlaylistsListSortOrder(sortOrder: SortOrder) = Unit

    override fun getPlaylistsListSortOrder(): Flow<SortOrder> = playlistSortOrder

    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) =
        Unit

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> = flow { }
}
