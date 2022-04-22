package com.sebastianvm.musicplayer.ui.queue

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    private val tracksRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<QueueUiEvent, QueueState>(
    initialState
) {

    init {
        viewModelScope.launch {
            playbackManager.getQueue().flatMapLatest { ids ->
                tracksRepository.getTracks(ids).map { tracks ->
                    tracks.sortedBy { track -> ids.indexOf(track.trackId) }
                }
            }.collect { tracks ->
                setState {
                    copy(
                        queueItems = tracks.mapIndexed { index, track ->
                            QueueItem(
                                index,
                                track.toTrackRowState(includeTrackNumber = false)
                            )
                        }
                    )
                }
            }
        }
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
                val index =
                    state.value.queueItems.indexOfFirst { it.trackRowState.trackId == action.trackId }
                if (index == -1) return
                playbackManager.playQueueItem(index)
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
    data class TrackClicked(val trackId: String) : QueueUserAction()
}

sealed class QueueUiEvent : UiEvent
