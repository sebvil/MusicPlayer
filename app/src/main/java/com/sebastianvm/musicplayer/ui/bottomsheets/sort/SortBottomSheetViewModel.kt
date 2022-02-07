package com.sebastianvm.musicplayer.ui.bottomsheets.sort

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class SortBottomSheetViewModel @Inject constructor(initialState: SortBottomSheetState) :
    BaseViewModel<SortBottomSheetUserAction, SortBottomSheetUiEvent, SortBottomSheetState>(
        initialState
    ) {

    override fun handle(action: SortBottomSheetUserAction) {
        when (action) {
            is SortBottomSheetUserAction.MediaSortOptionSelected -> {
                addUiEvent(SortBottomSheetUiEvent.CloseBottomSheet(action.sortOption))
            }
        }
    }
}

data class SortBottomSheetState(
    val sortOptions: List<Int>,
    val selectedSort: Int,
    val sortOrder: MediaSortOrder
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialSortBottomSheetState {
    @Provides
    @ViewModelScoped
    fun initialSortBottomSheetStateProvider(savedStateHandle: SavedStateHandle): SortBottomSheetState {
        // We should always pass these
        val screen = savedStateHandle.get<String>("screen")!!
        val selectedSort = savedStateHandle.get<Int>("sortOption")!!
        val sortOrder = savedStateHandle.get<String>("sortOrder")!!
        return SortBottomSheetState(
            sortOptions = getSortOptionsForScreen(screen),
            selectedSort = selectedSort,
            sortOrder = MediaSortOrder.valueOf(sortOrder)
        )
    }
}

fun getSortOptionsForScreen(screen: String): List<Int> {
    return when (screen) {
        NavRoutes.TRACKS_ROOT -> {
            listOf(R.string.track_name, R.string.artist_name, R.string.album_name)
        }
        NavRoutes.ALBUMS_ROOT -> {
            listOf(R.string.album_name, R.string.artist_name, R.string.year)
        }
        else -> listOf()
    }
}

sealed class SortBottomSheetUserAction : UserAction {
    data class MediaSortOptionSelected(@StringRes val sortOption: Int) : SortBottomSheetUserAction()
}

sealed class SortBottomSheetUiEvent : UiEvent {
    data class CloseBottomSheet(@StringRes val sortOption: Int) : SortBottomSheetUiEvent()
}

