package com.sebastianvm.musicplayer.repository.preferences

import com.sebastianvm.musicplayer.player.SavedPlaybackInfo
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortSettings
import com.sebastianvm.musicplayer.util.sort.SortSettings
import com.sebastianvm.musicplayer.util.sort.copy
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import com.sebastianvm.musicplayer.util.sort.sortSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FakePreferencesRepository(sortSettings: SortSettings = sortSettings {}) :
    PreferencesRepository {

    private val _savedSortSettings = MutableStateFlow(sortSettings)
    private var savedSortSettings
        get() = _savedSortSettings.value
        set(value) {
            _savedSortSettings.value = value
        }

    override suspend fun modifyTrackListSortOptions(
        mediaSortSettings: MediaSortSettings,
        tracksListType: TracksListType,
        tracksListName: String
    ) {
        savedSortSettings = when (tracksListType) {
            TracksListType.ALL_TRACKS -> {
                savedSortSettings.copy {
                    allTracksSortSettings = mediaSortSettings
                }
            }
            TracksListType.GENRE -> {
                savedSortSettings.copy {
                    genreTrackListSortSettings[tracksListName] = mediaSortSettings
                }
            }
            TracksListType.PLAYLIST -> {
                savedSortSettings.copy {
                    playlistTrackListSortSettings[tracksListName] = mediaSortSettings
                }
            }
        }
    }

    override fun getTracksListSortOptions(
        tracksListType: TracksListType,
        tracksListName: String
    ): Flow<MediaSortSettings> = flow {
        emit(when (tracksListType) {
            TracksListType.ALL_TRACKS -> {
                savedSortSettings.allTracksSortSettings
            }
            TracksListType.GENRE -> {
                savedSortSettings.genreTrackListSortSettingsMap[tracksListName]

            }
            TracksListType.PLAYLIST -> {
                savedSortSettings.playlistTrackListSortSettingsMap[tracksListName]

            }
        } ?: mediaSortSettings {})
    }.distinctUntilChanged()

    override suspend fun modifyAlbumsListSortOptions(mediaSortSettings: MediaSortSettings) {
        savedSortSettings = savedSortSettings.copy {
            albumsListSortSettings = mediaSortSettings
        }
    }

    override fun getAlbumsListSortOptions(): Flow<MediaSortSettings> =
        _savedSortSettings.map { it.albumsListSortSettings }.distinctUntilChanged()

    override suspend fun modifyArtistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        savedSortSettings = savedSortSettings.copy {
            artistListSortSettings = mediaSortOrder
        }
    }

    override fun getArtistsListSortOrder(): Flow<MediaSortOrder> =
        _savedSortSettings.map { it.artistListSortSettings }.distinctUntilChanged()

    override suspend fun modifyGenresListSortOrder(mediaSortOrder: MediaSortOrder) {
        savedSortSettings = savedSortSettings.copy {
            genresListSortSettings = mediaSortOrder
        }
    }

    override fun getGenresListSortOrder(): Flow<MediaSortOrder> =
        _savedSortSettings.map { it.genresListSortSettings }.distinctUntilChanged()

    override suspend fun modifyPlaylistsListSortOrder(mediaSortOrder: MediaSortOrder) {
        savedSortSettings = savedSortSettings.copy {
            playlistsListSortSettings = mediaSortOrder
        }
    }

    override fun getPlaylistsListSortOrder(): Flow<MediaSortOrder> = flow {
        emit(savedSortSettings.playlistsListSortSettings)
    }.distinctUntilChanged()

    override suspend fun modifySavedPlaybackInfo(transform: (savedPlaybackInfo: SavedPlaybackInfo) -> SavedPlaybackInfo) {
        TODO("Not yet implemented")
    }

    override fun getSavedPlaybackInfo(): Flow<SavedPlaybackInfo> {
        TODO("Not yet implemented")
    }


}
