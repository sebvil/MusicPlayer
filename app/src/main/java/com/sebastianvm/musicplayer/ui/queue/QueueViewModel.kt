package com.sebastianvm.musicplayer.ui.queue

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    private val tracksRepository: TrackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    preferencesRepository: PreferencesRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseViewModel<QueueUserAction, QueueUiEvent, QueueState>(initialState) {

    init {
        collect(preferencesRepository.getSavedPlaybackInfo()) { playbackInfo ->
            Log.i("QUEUE", "Updating ${playbackInfo.mediaId}")
            setState {
                copy(
                    mediaGroup = playbackInfo.currentQueue,
                    nowPlayingTrackId = playbackInfo.mediaId
                )
            }
            val tracks = tracksRepository.getTracksForQueue(playbackInfo.currentQueue).first()
            val mediaQueue = mediaQueueRepository.getQueue(playbackInfo.currentQueue).first()
            setState {
                copy(
                    chosenQueue = mediaQueue,
                    queueItems = tracks.map { track -> track.toTrackRowState(includeTrackNumber = false) }
                )
            }
        }

        collect(mediaQueueRepository.getAllQueues()) { queues ->
            setState {
                copy(
                    queues = queues
                )
            }
        }
    }

    override fun handle(action: QueueUserAction) {
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
                val index = state.value.queueItems.indexOf(action.item)
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
                        mediaPlaybackRepository.moveQueueItem(
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

                        chosenQueue?.also { mediaQueue ->
                            viewModelScope.launch {
                                mediaQueueRepository.insertOrUpdateMediaQueueTrackCrossRefs(
                                    queueItems.mapIndexed { index, trackRowState ->
                                        MediaQueueTrackCrossRef(
                                            mediaGroupType = mediaQueue.mediaGroupType,
                                            groupMediaId = mediaQueue.groupMediaId,
                                            trackId = trackRowState.trackId,
                                            trackIndex = index
                                        )
                                    })

                                val tracks =
                                    tracksRepository.getTracksForQueue(mediaQueue.toMediaGroup())
                                        .first()
                                setState {
                                    copy(
                                        chosenQueue = mediaQueue,
                                        queueItems = tracks.map { track ->
                                            track.toTrackRowState(
                                                includeTrackNumber = false
                                            )
                                        }
                                    )
                                }
                            }
                        }

                    }
                }
            }
            is QueueUserAction.TrackClicked -> {
                with(state.value) {
                    if (chosenQueue != null) {
                        if (mediaGroup?.mediaId != chosenQueue.groupMediaId || mediaGroup.mediaGroupType != chosenQueue.mediaGroupType) {
                            mediaPlaybackRepository.playFromId(
                                action.trackId,
                                MediaGroup(chosenQueue.mediaGroupType, chosenQueue.groupMediaId)

                            )
                            return
                        }
                    }
                    val index = state.value.queueItems.indexOfFirst { it.trackId == action.trackId }
                    if (index == -1) return
                    mediaPlaybackRepository.playQueueItem(index)
                }

            }
            is QueueUserAction.DropdownMenuClicked -> {
                setState {
                    copy(
                        dropdownExpanded = !dropdownExpanded
                    )
                }
            }
            is QueueUserAction.DropdownMenuOptionChosen -> {
                viewModelScope.launch {
                    val tracks = tracksRepository.getTracksForQueue(
                        MediaGroup(
                            action.newOption.mediaGroupType,
                            action.newOption.groupMediaId
                        )
                    ).first()
                        .map { it.toTrackRowState(includeTrackNumber = false) }
                    setState {
                        copy(
                            dropdownExpanded = false,
                            chosenQueue = action.newOption,
                            queueItems = tracks
                        )
                    }
                }
            }
        }
    }
}

data class QueueState(
    val queues: List<MediaQueue>,
    val chosenQueue: MediaQueue?,
    val dropdownExpanded: Boolean,
    val mediaGroup: MediaGroup?,
    val queueItems: List<TrackRowState>,
    val draggedItem: TrackRowState?,
    val draggedItemStartingIndex: Int = -1,
    val draggedItemFinalIndex: Int = -1,
    val nowPlayingTrackId: String,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialQueueStateModule {
    @Provides
    @ViewModelScoped
    fun initialQueueStateProvider(): QueueState {
        return QueueState(
            queues = listOf(),
            chosenQueue = null,
            dropdownExpanded = false,
            mediaGroup = null,
            queueItems = listOf(),
            draggedItem = null,
            nowPlayingTrackId = "",
        )
    }
}

sealed class QueueUserAction : UserAction {
    data class ItemDragged(val newIndex: Int) : QueueUserAction()
    data class ItemSelectedForDrag(val item: TrackRowState) : QueueUserAction()
    object DragEnded : QueueUserAction()
    data class TrackClicked(val trackId: String) : QueueUserAction()
    object DropdownMenuClicked : QueueUserAction()
    data class DropdownMenuOptionChosen(val newOption: MediaQueue) : QueueUserAction()
}

sealed class QueueUiEvent : UiEvent
