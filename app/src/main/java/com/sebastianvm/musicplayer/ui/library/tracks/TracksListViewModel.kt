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
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.extensions.MEDIA_METADATA_COMPAT_KEY
import com.sebastianvm.musicplayer.util.extensions.album
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


enum class SortOption(@StringRes val id: Int, val metadataKey: String) {
    TRACK_NAME(R.string.track_name, MediaMetadataCompat.METADATA_KEY_TITLE),
    ARTIST_NAME(R.string.artist_name, MediaMetadataCompat.METADATA_KEY_ARTIST),
    ALBUM_NAME(R.string.album_name, MediaMetadataCompat.METADATA_KEY_ALBUM);

    companion object {
        fun fromResId(@StringRes resId: Int): SortOption {
            return when (resId) {
                R.string.track_name -> TRACK_NAME
                R.string.artist_name -> ARTIST_NAME
                R.string.album_name -> ALBUM_NAME
                else -> throw IllegalStateException("Unknown sort option for tracks list")
            }
        }
    }
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
        val albumName = meta.album ?: return null
        return TrackRowState(id, trackName, artists, albumName)
    }

    override fun handle(action: TracksListUserAction) {
        when (action) {
            is TracksListUserAction.TrackClicked -> {
                val transportControls = musicServiceConnection.transportControls
                val parentId = when (val title = state.value.tracksListTitle) {
                    is DisplayableString.StringValue -> "genre-${title.value}"
                    else -> BrowseTree.TRACKS_ROOT
                }
                val extras = Bundle().apply {
                    putString(
                        PARENT_ID,
                        parentId
                    )
                    putString(
                        SORT_BY,
                        state.value.currentSort.metadataKey
                    )
                    putString(SORT_ORDER, state.value.sortOrder.name)
                }
                transportControls.playFromMediaId(action.trackGid, extras)
                addUiEvent(TracksListUiEvent.NavigateToPlayer)
            }
            is TracksListUserAction.SortByClicked -> {
                addUiEvent(TracksListUiEvent.ShowBottomSheet(state.value.currentSort.id, state.value.sortOrder))
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
            is TracksListUserAction.TrackLongPressed -> {
                addUiEvent(
                    TracksListUiEvent.OpenContextMenu(action.trackGid)
                )
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
                SortOption.ALBUM_NAME -> trackRowState.albumName
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
        val genreName = savedStateHandle.get<String?>(NavArgs.GENRE_NAME)
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
    data class TrackLongPressed(val trackGid: String) : TracksListUserAction()
}

sealed class TracksListUiEvent : UiEvent {
    object NavigateToPlayer : TracksListUiEvent()
    data class ShowBottomSheet(@StringRes val sortOption: Int, val sortOrder: SortOrder) : TracksListUiEvent()
    data class OpenContextMenu(val trackGid: String) : TracksListUiEvent()
}


