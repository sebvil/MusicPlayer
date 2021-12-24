package com.sebastianvm.musicplayer.ui.queue

import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    tracksRepository: TrackRepository,
    musicServiceConnection: MusicServiceConnection,

    ) : BaseViewModel<QueueUserAction, QueueUiEvent, QueueState>(initialState) {

    init {
        collect(musicServiceConnection.currentQueueId) { queueId ->
            setState {
                copy(
                    queueId = queueId
                )
            }
            queueId?.also {
                collect(tracksRepository.getTracksForQueue(it)) { tracks ->
                    setState {
                        copy(
                            queueItems = tracks.map { track -> track.toTrackRowState() }
                        )
                    }
                }
            }
        }
    }

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.ItemDragged -> {
                val oldIndex = state.value.draggedItemIndex
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
                            draggedItemIndex = action.newIndex
                        )
                    }
                }
            }
            is QueueUserAction.ItemSelectedForDrag -> {
                val index = state.value.queueItems.indexOf(action.index)
                val items = state.value.queueItems.toMutableList()
                val itemToDrag = items[index]
                items[index] = itemToDrag.copy(trackName = "", artists = "")
                setState {
                    copy(
                        draggedItem = itemToDrag,
                        draggedItemIndex = index,
                        queueItems = items
                    )
                }
            }
            is QueueUserAction.DragEnded -> {
                state.value.draggedItem?.also {
                    val items = state.value.queueItems.toMutableList()
                    items[state.value.draggedItemIndex] = it
                    setState {
                        copy(
                            queueItems = items,
                            draggedItemIndex = -1,
                            draggedItem = null
                        )
                    }
                }

            }
        }
    }
}

data class QueueState(
    val queueId: Long?,
    val queueItems: List<TrackRowState>,
    val draggedItem: TrackRowState?,
    val draggedItemIndex: Int = -1,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialQueueStateModule {
    @Provides
    @ViewModelScoped
    fun initialQueueStateProvider(): QueueState {
        return QueueState(queueId = null, queueItems = listOf(), draggedItem = null)
    }
}

sealed class QueueUserAction : UserAction {
    data class ItemDragged(val oldIndex: Int, val newIndex: Int) : QueueUserAction()
    data class ItemSelectedForDrag(val index: TrackRowState) : QueueUserAction()
    object DragEnded : QueueUserAction()
}

sealed class QueueUiEvent : UiEvent

