package com.sebastianvm.musicplayer.ui.library.tracks

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.PARENT_ID
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.player.SORT_ORDER
import com.sebastianvm.musicplayer.repository.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.extensions.MEDIA_METADATA_COMPAT_KEY
import com.sebastianvm.musicplayer.util.extensions.artist
import com.sebastianvm.musicplayer.util.extensions.id
import com.sebastianvm.musicplayer.util.extensions.title
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


enum class SortOption(@StringRes val id: Int) {
    TRACK_NAME(R.string.track_name),
    ARTIST_NAME(R.string.artist_name)
}

data class TracksSortSettings(
    val sortOption: SortOption,
    val sortOrder: SortOrder
)


@HiltViewModel
class TracksListViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val preferencesRepository: PreferencesRepository,
    initialState: TracksListState
) : BaseViewModel<TracksListUserAction, TracksListUiEvent, TracksListState>(
    initialState
) {

    init {

        val genreName = state.value.genreName

        viewModelScope.launch {
            preferencesRepository.getTrackSortOptions(genreName = genreName).collect { settings ->
                setState {
                    copy(
                        currentSort = settings.sortOption,
                        tracksList = tracksList.sortedWith(
                            getComparator(
                                settings.sortOrder,
                                settings.sortOption
                            )
                        ),
                        sortOrder = settings.sortOrder
                    )
                }
            }

        }


        genreName?.also {
            musicServiceConnection.subscribe(
                "genre-$genreName",
                object : MediaBrowserCompat.SubscriptionCallback() {
                    override fun onChildrenLoaded(
                        parentId: String,
                        children: MutableList<MediaBrowserCompat.MediaItem>
                    ) {
                        super.onChildrenLoaded(parentId, children)
                        setState {
                            copy(
                                tracksList = children.mapNotNull { child ->
                                    child.description.toTrackRowState()
                                }.sortedWith(
                                    getComparator(
                                        sortOrder,
                                        currentSort
                                    )
                                )
                            )
                        }
                    }
                }
            )
        } ?: kotlin.run {

            musicServiceConnection.subscribe(
                BrowseTree.TRACKS_ROOT,
                object : MediaBrowserCompat.SubscriptionCallback() {
                    override fun onChildrenLoaded(
                        parentId: String,
                        children: MutableList<MediaBrowserCompat.MediaItem>
                    ) {
                        super.onChildrenLoaded(parentId, children)
                        setState {
                            copy(
                                tracksList = children.mapNotNull { child ->
                                    child.description.toTrackRowState()
                                }.sortedWith(
                                    getComparator(
                                        sortOrder,
                                        currentSort
                                    )
                                )
                            )
                        }
                    }
                }
            )
        }
    }

    fun MediaDescriptionCompat.toTrackRowState(): TrackRowState? {
        val meta =
            extras?.getParcelable<MediaMetadataCompat>(MEDIA_METADATA_COMPAT_KEY) ?: return null
        val id = meta.id ?: return null
        val trackName = meta.title ?: return null
        val artists = meta.artist ?: return null
        return TrackRowState(id, trackName, artists)
    }

    override fun handle(action: TracksListUserAction) {
        when (action) {
            is TracksListUserAction.TrackClicked -> {
                val transportControls = musicServiceConnection.transportControls
                val parentId = when (val title = state.value.tracksListTitle) {
                    is DisplayableString.StringValue -> "genre-${title.value}"
                    is DisplayableString.ResourceValue -> BrowseTree.TRACKS_ROOT
                }
                val extras = Bundle().apply {
                    putString(
                        PARENT_ID,
                        parentId
                    )
                    putString(
                        SORT_BY,
                        when (state.value.currentSort) {
                            SortOption.TRACK_NAME -> MediaMetadataCompat.METADATA_KEY_TITLE
                            SortOption.ARTIST_NAME -> MediaMetadataCompat.METADATA_KEY_ARTIST
                        }
                    )
                    putString(SORT_ORDER, state.value.sortOrder.name)
                }
                transportControls.playFromMediaId(action.trackGid, extras)
                addUiEvent(TracksListUiEvent.NavigateToPlayer)
            }
            is TracksListUserAction.SortByClicked -> {
                addUiEvent(TracksListUiEvent.ShowBottomSheet)
            }
            is TracksListUserAction.SortOptionClicked -> {
                val sortOrder = if (action.newSortOption == state.value.currentSort) {
                    !state.value.sortOrder
                } else {
                    state.value.sortOrder
                }

                setState {
                    copy(
                        currentSort = action.newSortOption,
                        tracksList = tracksList.sortedWith(
                            getComparator(
                                sortOrder,
                                action.newSortOption
                            )
                        ),
                        sortOrder = sortOrder
                    )
                }
                viewModelScope.launch {
                    preferencesRepository.modifyTrackListSortOptions(
                        sortOrder,
                        action.newSortOption,
                        state.value.genreName
                    )
                }
            }
        }
    }

    private fun getComparator(
        sortOrder: SortOrder,
        sortOption: SortOption
    ): Comparator<TrackRowState> {
        return getStringComparator(sortOrder) { trackRowState ->
            when (sortOption) {
                SortOption.TRACK_NAME -> trackRowState.trackName
                SortOption.ARTIST_NAME -> trackRowState.artists
            }
        }
    }


}


data class TracksListState(
    val genreName: String?,
    val tracksListTitle: DisplayableString,
    val tracksList: List<TrackRowState>,
    val currentSort: SortOption,
    val sortOrder: SortOrder
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialTracksListStateModule {
    @Provides
    @ViewModelScoped
    fun initialTracksListStateProvider(savedStateHandle: SavedStateHandle): TracksListState {
        val genreName = savedStateHandle.get<String?>("genreName")
        return TracksListState(
            genreName = genreName,
            tracksListTitle = genreName?.let { DisplayableString.StringValue(it) }
                ?: DisplayableString.ResourceValue(R.string.all_songs),
            tracksList = listOf(),
            currentSort = SortOption.TRACK_NAME,
            sortOrder = SortOrder.ASCENDING
        )
    }
}

sealed class TracksListUserAction : UserAction {
    data class TrackClicked(val trackGid: String) : TracksListUserAction()
    object SortByClicked : TracksListUserAction()
    data class SortOptionClicked(val newSortOption: SortOption) : TracksListUserAction()
}

sealed class TracksListUiEvent : UiEvent {
    object NavigateToPlayer : TracksListUiEvent()
    object ShowBottomSheet : TracksListUiEvent()
}


