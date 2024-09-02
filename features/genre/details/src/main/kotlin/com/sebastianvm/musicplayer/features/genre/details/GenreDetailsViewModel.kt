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
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.sort.sortMenu
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.trackContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
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
    private val args: GenreDetailsArguments,
    private val navController: NavController,
    vmScope: CoroutineScope = getViewModelScope(),
    genreRepository: GenreRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
    private val features: FeatureRegistry,
) : BaseViewModel<GenreDetailsState, GenreDetailsUserAction>(viewModelScope = vmScope) {

    private val sortPreferences =
        sortPreferencesRepository.getTrackListSortPreferences(MediaGroup.Genre(args.genreId))

    override val state: StateFlow<GenreDetailsState> =
        combine(genreRepository.getGenre(genreId = args.genreId), sortPreferences) { genre,
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
                initialValue = GenreDetailsState.Loading(args.genreName),
            )

    override fun handle(action: GenreDetailsUserAction) {
        when (action) {
            is GenreDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    features
                        .trackContextMenu()
                        .trackContextMenuUiComponent(
                            arguments =
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                trackPositionInList = action.trackPositionInList,
                                trackList = MediaGroup.Genre(args.genreId),
                            ),
                            navController = navController,
                        ),
                    navOptions =
                    NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }

            is GenreDetailsUserAction.SortButtonClicked -> {
                navController.push(
                    features
                        .sortMenu()
                        .sortMenuUiComponent(
                            arguments =
                            SortMenuArguments(listType = SortableListType.Genre(args.genreId))
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
                        mediaGroup = MediaGroup.Genre(args.genreId),
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}
