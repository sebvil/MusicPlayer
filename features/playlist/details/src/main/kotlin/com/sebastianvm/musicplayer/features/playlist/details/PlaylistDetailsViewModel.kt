package com.sebastianvm.musicplayer.features.playlist.details

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TextFieldDialog
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsProps
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchProps
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.trackSearch
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.sort.sortMenu
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuProps
import com.sebastianvm.musicplayer.features.api.track.menu.trackContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PlaylistDetailsState : State {
    val playlistName: String

    data class Loading(override val playlistName: String) : PlaylistDetailsState

    data class Data(
        val tracks: List<TrackRow.State>,
        val sortButtonState: SortButton.State,
        override val playlistName: String,
        val playlistNameDialog: TextFieldDialog.State?,
    ) : PlaylistDetailsState
}

sealed interface PlaylistDetailsUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long, val trackPositionInList: Int) :
        PlaylistDetailsUserAction

    data class TrackClicked(val trackIndex: Int) : PlaylistDetailsUserAction

    data object SortButtonClicked : PlaylistDetailsUserAction

    data object BackClicked : PlaylistDetailsUserAction

    data object AddTracksButtonClicked : PlaylistDetailsUserAction

    data object EditPlaylistNameClicked : PlaylistDetailsUserAction
}

class PlaylistDetailsViewModel(
    private val arguments: PlaylistDetailsArguments,
    private val props: StateFlow<PlaylistDetailsProps>,
    viewModelScope: CoroutineScope = getViewModelScope(),
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
    private val playlistRepository: PlaylistRepository,
    private val features: FeatureRegistry,
) :
    BaseViewModel<PlaylistDetailsState, PlaylistDetailsUserAction>(
        viewModelScope = viewModelScope
    ) {

    private val navController: NavController
        get() = props.value.navController

    private val _playlistNameDialog: MutableStateFlow<TextFieldDialog.State?> =
        MutableStateFlow(null)

    override val state: StateFlow<PlaylistDetailsState> =
        combine(
                playlistRepository.getPlaylist(arguments.playlistId),
                sortPreferencesRepository.getPlaylistSortPreferences(arguments.playlistId),
                _playlistNameDialog,
            ) { playlist, sortPrefs, playlistNameDialog ->
                PlaylistDetailsState.Data(
                    tracks = playlist.tracks.map { track -> TrackRow.State.fromTrack(track) },
                    playlistName = playlist.name,
                    sortButtonState =
                        sortPrefs.let {
                            SortButton.State(
                                option = sortPrefs.sortOption,
                                sortOrder = sortPrefs.sortOrder,
                            )
                        },
                    playlistNameDialog = playlistNameDialog,
                )
            }
            .stateIn(
                this.viewModelScope,
                SharingStarted.Lazily,
                PlaylistDetailsState.Loading(arguments.playlistName),
            )

    override fun handle(action: PlaylistDetailsUserAction) {
        when (action) {
            is PlaylistDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    features
                        .trackContextMenu()
                        .create(
                            arguments =
                                TrackContextMenuArguments(
                                    trackId = action.trackId,
                                    trackPositionInList = action.trackPositionInList,
                                    trackList =
                                        MediaGroup.Playlist(playlistId = arguments.playlistId),
                                ),
                            props =
                                MutableStateFlow(
                                    TrackContextMenuProps(navController = navController)
                                ),
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is PlaylistDetailsUserAction.SortButtonClicked -> {
                navController.push(
                    features
                        .sortMenu()
                        .create(
                            arguments =
                                SortMenuArguments(
                                    listType = SortableListType.Playlist(arguments.playlistId)
                                )
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is PlaylistDetailsUserAction.BackClicked -> {
                navController.pop()
            }
            is PlaylistDetailsUserAction.TrackClicked -> {
                viewModelScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.Playlist(arguments.playlistId),
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
            is PlaylistDetailsUserAction.AddTracksButtonClicked -> {
                navController.push(
                    features
                        .trackSearch()
                        .create(
                            arguments = TrackSearchArguments(playlistId = arguments.playlistId),
                            props =
                                MutableStateFlow(TrackSearchProps(navController = navController)),
                        )
                )
            }
            is PlaylistDetailsUserAction.EditPlaylistNameClicked -> {
                _playlistNameDialog.value =
                    TextFieldDialog.State(
                        title = RString.edit_playlist_name,
                        confirmButtonText = RString.save,
                        initialText = state.value.playlistName,
                        onSave = { newPlaylistName ->
                            if (newPlaylistName.isBlank()) {
                                _playlistNameDialog.update {
                                    it?.copy(errorMessage = RString.playlist_name_cannot_be_empty)
                                }
                                return@State
                            }
                            viewModelScope.launch {
                                playlistRepository.updatePlaylistName(
                                    arguments.playlistId,
                                    newPlaylistName,
                                )
                            }
                            _playlistNameDialog.value = null
                        },
                        onDismiss = { _playlistNameDialog.value = null },
                    )
            }
        }
    }
}
