package com.sebastianvm.musicplayer.ui.queue

import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.COMMAND_MOVE_ITEM
import com.sebastianvm.musicplayer.player.EXTRA_FROM_INDEX
import com.sebastianvm.musicplayer.player.EXTRA_TO_INDEX
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    tracksRepository: TrackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val musicServiceConnection: MusicServiceConnection,

    ) : BaseViewModel<QueueUserAction, QueueUiEvent, QueueState>(initialState) {

    init {
        collect(musicServiceConnection.currentQueueId) { mediaGroup ->
            setState {
                copy(
                    mediaGroup = mediaGroup
                )
            }
            mediaGroup?.also {
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
                    musicServiceConnection.sendCommand(COMMAND_MOVE_ITEM, bundleOf(EXTRA_FROM_INDEX to oldIndex, EXTRA_TO_INDEX to action.newIndex))
                    setState {
                        copy(
                            queueItems = items,
                            draggedItemIndex = action.newIndex
                        )
                    }
                }
            }
            is QueueUserAction.ItemSelectedForDrag -> {
                val index = state.value.queueItems.indexOf(action.item)
                val items = state.value.queueItems.toMutableList()
                val itemToDrag = items[index]
                setState {
                    copy(
                        draggedItem = itemToDrag,
                        draggedItemIndex = index,
                        queueItems = items
                    )
                }
            }
            is QueueUserAction.DragEnded -> {
                with(state.value) {
                    draggedItem?.also {
                        val items = queueItems.toMutableList()
                        items[draggedItemIndex] = it
                        setState {
                            copy(
                                queueItems = items,
                                draggedItemIndex = -1,
                                draggedItem = null
                            )
                        }

                        mediaGroup?.also { mediaGroup ->
                            viewModelScope.launch {
                                mediaQueueRepository.insertOrUpdateMediaQueueTrackCrossRefs(
                                    queueItems.mapIndexed { index, trackRowState ->
                                        MediaQueueTrackCrossRef(
                                            mediaType = mediaGroup.mediaType,
                                            groupMediaId = mediaGroup.mediaId,
                                            trackId = trackRowState.trackId,
                                            trackIndex = index
                                        )
                                    })
                            }
                        }

                    }
                }


            }
        }
    }
}

data class QueueState(
    val mediaGroup: MediaGroup?,
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
        return QueueState(mediaGroup = null, queueItems = listOf(), draggedItem = null)
    }
}

sealed class QueueUserAction : UserAction {
    data class ItemDragged(val newIndex: Int) : QueueUserAction()
    data class ItemSelectedForDrag(val item: TrackRowState) : QueueUserAction()
    object DragEnded : QueueUserAction()
}

sealed class QueueUiEvent : UiEvent

