package com.sebastianvm.musicplayer.features.genre.details

import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GenreDetailsArguments(val genreId: Long, val genreName: String) : Arguments

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

class GenreDetailsStateHolder(
    private val args: GenreDetailsArguments,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    genreRepository: GenreRepository,
    sortPreferencesRepository:
        com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository,
    private val playbackManager: PlaybackManager,
) : StateHolder<GenreDetailsState, GenreDetailsUserAction> {

    private val sortPreferences =
        sortPreferencesRepository.getTrackListSortPreferences(MediaGroup.Genre(args.genreId))

    override val state: StateFlow<GenreDetailsState> =
        combine(genreRepository.getGenre(genreId = args.genreId), sortPreferences) {
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
                scope = stateHolderScope,
                started = SharingStarted.Lazily,
                initialValue = GenreDetailsState.Loading(args.genreName),
            )

    override fun handle(action: GenreDetailsUserAction) {
        when (action) {
            is GenreDetailsUserAction.TrackMoreIconClicked -> {
                navController.push(
                    TrackContextMenu(
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
                    SortMenuUiComponent(
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
                stateHolderScope.launch {
                    playbackManager.playMedia(
                        mediaGroup = MediaGroup.Genre(args.genreId),
                        initialTrackIndex = action.trackIndex,
                    )
                }
            }
        }
    }
}
