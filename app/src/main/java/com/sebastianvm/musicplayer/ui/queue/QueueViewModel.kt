package com.sebastianvm.musicplayer.ui.queue

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    private val playbackManager: PlaybackManager,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel<QueueUiEvent, QueueState>(
    initialState
) {

    init {
        viewModelScope.launch {
            playbackManager.getSavedPlaybackInfo().collect { savedPlaybackInfo ->
                setState {
                    copy(
                        queueItems = savedPlaybackInfo.queuedTracks.mapIndexed { index, track ->
                            QueueItem(
                                index,
                                track.toTrackRowState(includeTrackNumber = false)
                            )
                        },
                        nowPlayingTrackIndex = savedPlaybackInfo.nowPlayingIndex
                    )
                }
            }
        }
    }

    fun itemSelectedForDrag(position: Int) {
        setState {
            copy(
                draggedItemFinalIndex = position
            )
        }
    }

    fun onMove(from: Int, to: Int) {
        Log.i("MOVE", "from: $from, to: $to")
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

    fun <A : UserAction> handle(action: A) {
        when (action) {
            is QueueUserAction.ItemDragged -> {
                val oldIndex = state.value.draggedItemFinalIndex
                if (oldIndex != action.newIndex) {
                    if (action.newIndex !in state.value.queueItems.indices || oldIndex !in state.value.queueItems.indices) {
                        return
                    }
                    val items = state.value.queueItems.toMutableList()
                    val item = items.removeAt(oldIndex)
                    items.add(action.newIndex, item)
                    setState {
                        copy(
                            queueItems = items,
                            draggedItemFinalIndex = action.newIndex
                        )
                    }
                }
            }
            is QueueUserAction.ItemSelectedForDrag -> {
                val index = action.index
                val items = state.value.queueItems.toMutableList()
                val itemToDrag = items[index]
                setState {
                    copy(
                        draggedItem = itemToDrag,
                        draggedItemStartingIndex = index,
                        draggedItemFinalIndex = index,
                        queueItems = items
                    )
                }
            }
            is QueueUserAction.DragEnded -> {
                with(state.value) {
                    draggedItem?.also {
                        val items = queueItems.toMutableList()
                        playbackManager.moveQueueItem(
                            previousIndex = draggedItemStartingIndex,
                            newIndex = draggedItemFinalIndex
                        )
                        items[draggedItemFinalIndex] = it
                        setState {
                            copy(
                                queueItems = items,
                                draggedItemFinalIndex = -1,
                                draggedItemStartingIndex = -1,
                                draggedItem = null
                            )
                        }
                    }
                }
            }
            is QueueUserAction.TrackClicked -> {
                playbackManager.playQueueItem(action.trackIndex)
            }
        }
    }
}

data class QueueItem(val queuePosition: Int, val trackRowState: TrackRowState)

data class QueueState(
    val mediaGroup: MediaGroup?,
    val queueItems: List<QueueItem>,
    val draggedItem: QueueItem?,
    val draggedItemStartingIndex: Int = -1,
    val draggedItemFinalIndex: Int = -1,
    val nowPlayingTrackIndex: Int
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
            draggedItem = null,
            nowPlayingTrackIndex = 0,
        )
    }
}

sealed class QueueUserAction : UserAction {
    data class ItemDragged(val newIndex: Int) : QueueUserAction()
    data class ItemSelectedForDrag(val index: Int) : QueueUserAction()
    object DragEnded : QueueUserAction()
    data class TrackClicked(val trackIndex: Int) : QueueUserAction()
}

sealed class QueueUiEvent : UiEvent
