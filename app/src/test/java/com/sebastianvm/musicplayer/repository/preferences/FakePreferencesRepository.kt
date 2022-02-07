package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.SortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

class FakePreferencesRepository(trackSortOption: MediaSortOption.= MediaSortOption.TRACK) :
    PreferencesRepository {

    private val albumSortSettings =
        MutableStateFlow(SortSettings(MediaSortOption.ALBUM, MediaSortOrder.ASCENDING))
    private val trackSortSettings =
        MutableStateFlow(SortSettings(trackSortOption, MediaSortOrder.ASCENDING))
    private val artistSortOrder = MutableStateFlow(MediaSortOrder.ASCENDING)
    private val genresSortOrder = MutableStateFlow(MediaSortOrder.ASCENDING)
    private val playlistSortOrder = MutableStateFlow(MediaSortOrder.ASCENDING)


    override suspend fun modifyTrackListSortOptions(
        sortSettings: SortSettings,
        genreName: String?
    ) = trackSortSettings.emit(sortSettings)

    override fun getTracksListSortOptions(genreName: String?): Flow<SortSettings> =
        trackSortSettings

    override suspend fun modifyAlbumsListSortOptions(sortSettings: SortSettings) =
        albumSortSettings.emit(sortSettings)

    override fun getAlbumsListSortOptions(): Flow<SortSettings> = albumSortSettings


    override suspend fun modifyArtistsListSortOrder(sortOrder: MediaSortOrder) {
        artistSortOrder.emit(!artistSortOrder.value)
    }

    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> = artistSortOrder

    override suspend fun modifyGenresListSortOrder(sortOrder: MediaSortOrder) =
        genresSortOrder.emit(sortOrder)

    override fun getGenresListSortOrder(): Flow<MediaSortOrder> = genresSortOrder
    override suspend fun modifyPlaylistsListSortOrder(sortOrder: MediaSortOrder) = Unit

    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> = playlistSortOrder

    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) =
        Unit

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> = flow { }
}
