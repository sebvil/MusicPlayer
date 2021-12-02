package com.sebastianvm.musicplayer.ui.library.tracks

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.PARENT_ID
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.extensions.MEDIA_METADATA_COMPAT_KEY
import com.sebastianvm.musicplayer.util.extensions.artist
import com.sebastianvm.musicplayer.util.extensions.id
import com.sebastianvm.musicplayer.util.extensions.title
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject



enum class SortOption(@StringRes val id: Int) {
    TRACK_NAME(R.string.track_name),
    ARTIST_NAME(R.string.artist_name)
}


@HiltViewModel
class TracksListViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    initialState: TracksListState
) : BaseViewModel<TracksListUserAction, TracksListUiEvent, TracksListState>(initialState) {
    init {
        when (val title = state.value.tracksListTitle) {
            is DisplayableString.StringValue -> {
                musicServiceConnection.subscribe(
                    "genre-${title.value}",
                    object : MediaBrowserCompat.SubscriptionCallback() {
                        override fun onChildrenLoaded(
                            parentId: String,
                            children: MutableList<MediaBrowserCompat.MediaItem>
                        ) {
                            super.onChildrenLoaded(parentId, children)
                            setState {
                                copy(
                                    tracksList =
                                    children.mapNotNull { child ->
                                        child.description.toTrackRowState()
                                    }.sortedBy { item -> item.trackName },
                                )
                            }
                        }
                    }
                )
            }
            is DisplayableString.ResourceValue -> {
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
                                    }.sortedBy { item -> item.trackName },
                                )
                            }
                        }
                    }
                )
            }
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
                        MediaMetadataCompat.METADATA_KEY_TITLE
                    )
                }
                transportControls.playFromMediaId(action.trackGid, extras)
                addUiEvent(TracksListUiEvent.NavigateToPlayer)
            }
            is TracksListUserAction.SortByClicked -> {
                addUiEvent(TracksListUiEvent.ShowBottomSheet)
            }
            is TracksListUserAction.SortOptionClicked -> {
                setState {
                    copy(
                        currentSort = action.newSortOption,
                        tracksList = tracksList.sortedBy { track ->
                            when (action.newSortOption) {
                                SortOption.TRACK_NAME -> track.trackName
                                SortOption.ARTIST_NAME -> track.artists
                            }
                        }
                    )
                }
            }
        }
    }
}


data class TracksListState(
    val tracksListTitle: DisplayableString,
    val tracksList: List<TrackRowState>,
    val currentSort: SortOption
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialTracksListStateModule {
    @Provides
    @ViewModelScoped
    fun initialTracksListStateProvider(savedStateHandle: SavedStateHandle): TracksListState {
        val genreName = savedStateHandle.get<String?>("genreName")
        return TracksListState(
            tracksListTitle = genreName?.let { DisplayableString.StringValue(it) }
                ?: DisplayableString.ResourceValue(R.string.all_songs),
            tracksList = listOf(),
            currentSort = SortOption.TRACK_NAME,
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


