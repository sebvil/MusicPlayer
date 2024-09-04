package com.sebastianvm.musicplayer.features.genre.details

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsProps
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
import kotlinx.coroutines.launch

sealed interface GenreDetailsState : State {
    val genreName: String

    data class Loading(override val genreName: String) : GenreDetailsState

    data class Data(
        val tracks: List<TrackRow.State>,
        val sortButtonState: SortButton.State,
        override val genreName: String,
    ) : GenreDetailsState
}

sealed interface GenreDetailsUserAction : UserAction {
    data class TrackMoreIconClicked(val trackId: Long, val trackPositionInList: Int) :
        GenreDetailsUserAction

    data class TrackClicked(val trackIndex: Int) : GenreDetailsUserAction

    data object SortButtonClicked : GenreDetailsUserAction

    data object BackClicked : GenreDetailsUserAction
}

class GenreDetailsViewModel(
    private val arguments: GenreDetailsArguments,
    private val props: StateFlow<GenreDetailsProps>,
    vmScope: CoroutineScope = getViewModelScope(),
    genreRepository: GenreRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : BaseViewModel<GenreDetailsState, GenreDetailsUserAction>(viewModelScope = vmScope) {

    private val navController: NavController
        get() = props.value.navController

    private val sortPreferences =
        sortPreferencesRepository.getTrackListSortPreferences(MediaGroup.Genre(arguments.genreId))

    override val state: StateFlow<GenreDetailsState> =
        combine(genreRepository.getGenre(genreId = arguments.genreId), sortPreferences) {
                genre,
                sortPrefs ->
                GenreDetailsState.Data(
                    tracks = genre.tracks.map { track -> TrackRow.State.fromTrack(track) },
                    sortButtonState =
                        SortButton.State(
                            option = sortPrefs.sortOption,
                            sortOrder = sortPrefs.sortOrder,
                        ),
                    genreName = genre.name,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = GenreDetailsState.Loading(arguments.genreName),
            )

    override fun handle(action: GenreDetailsUserAction) {
        when (action) {
            is GenreDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    features
                        .trackContextMenu()
                        .create(
                            arguments =
                                TrackContextMenuArguments(
                                    trackId = action.trackId,
                                    trackPositionInList = action.trackPositionInList,
                                    trackList = MediaGroup.Genre(arguments.genreId),
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
            is GenreDetailsUserAction.SortButtonClicked -> {
                navController.push(
                    features
                        .sortMenu()
                        .create(
                            arguments =
                                SortMenuArguments(
                                    listType = SortableListType.Genre(arguments.genreId)
                                )
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is GenreDetailsUserAction.BackClicked -> {
                navController.pop()
            }
            is GenreDetailsUserAction.TrackClicked -> {
                viewModelScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.Genre(arguments.genreId),
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}
