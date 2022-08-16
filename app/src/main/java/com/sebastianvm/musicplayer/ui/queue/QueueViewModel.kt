package com.sebastianvm.musicplayer.ui.queue

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.DraggableTrackRowState
import com.sebastianvm.musicplayer.ui.components.toDraggableTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<QueueUiEvent, QueueState>(
    initialState
) {

    init {
        viewModelScope.launch {
            playbackManager.getSavedPlaybackInfo().collect { savedPlaybackInfo ->
                setState {
                    copy(
                        queueItems = savedPlaybackInfo.queuedTracks.map { track ->
                            track.toDraggableTrackRowState()
                        },
                        nowPlayingId = savedPlaybackInfo.nowPlayingId
                    )
                }

                addUiEvent(
                    QueueUiEvent.ScrollToNowPlayingItem(
                        savedPlaybackInfo.queuedTracks.indexOfFirst { it.uniqueQueueItemId == savedPlaybackInfo.nowPlayingId })
                )
            }
        }

    }

    fun onItemSelectedForDrag(position: Int) {
        setState {
            copy(
                draggedItemFinalIndex = position
            )
        }
    }

    fun onMove(to: Int) {
        val oldIndex = state.value.draggedItemFinalIndex
        if (oldIndex != to) {
            val items = state.value.queueItems.toMutableList()
            val item = items.removeAt(oldIndex)
            items.add(to, item)
            setState {
                copy(
                    queueItems = items,
                    draggedItemFinalIndex = to
                )
            }
        }

    }

    fun onDragEnded(initialPosition: Int, finalPosition: Int) {
        playbackManager.moveQueueItem(initialPosition, finalPosition)
        setState { copy(draggedItemFinalIndex = -1) }
    }

    fun onTrackClicked(trackIndex: Int) {
        playbackManager.playQueueItem(trackIndex)
    }

    fun onTrackOverflowMenuClicked(trackId: String) {}


}

data class QueueState(
    val mediaGroup: MediaGroup?,
    val queueItems: List<DraggableTrackRowState>,
    val draggedItemFinalIndex: Int = -1,
    val nowPlayingId: String
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialQueueStateModule {
    @Provides
    @ViewModelScoped
    fun initialQueueStateProvider(): QueueState {
        return QueueState(
            mediaGroup = null,
            queueItems = listOf(),
            nowPlayingId = "",
        )
    }
}

sealed class QueueUiEvent : UiEvent {
    data class ScrollToNowPlayingItem(val index: Int) : QueueUiEvent()
}
