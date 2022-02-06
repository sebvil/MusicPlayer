package com.sebastianvm.musicplayer.ui.queue

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
import com.sebastianvm.musicplayer.ui.util.mvvm.launchViewModelIOScope
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class QueueViewModel @Inject constructor(
    initialState: QueueState,
    private val tracksRepository: TrackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    preferencesRepository: PreferencesRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseViewModel<QueueUserAction, QueueUiEvent, QueueState>(
    initialState
) {

    init {
        collect(preferencesRepository.getSavedPlaybackInfo()) { playbackInfo ->
            setState {
                copy(
                    mediaGroup = playbackInfo.currentQueue,
                )
            }
            val mediaQueue = mediaQueueRepository.getQueue(playbackInfo.currentQueue).first()
            setState {
                copy(
                    chosenQueue = chosenQueue ?: mediaQueue,
                )
            }
        }
        collect(state.map { it.chosenQueue }.distinctUntilChanged().flatMapLatest { queue ->
            queue?.let {
                tracksRepository.getTracksForQueue(it.toMediaGroup())
            } ?: flow {}
        }) { tracks ->
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

        collect(mediaPlaybackRepository.nowPlayingIndex) {
            setState {
                copy(
                    nowPlayingTrackIndex = it
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
                            launchViewModelIOScope {
                                mediaQueueRepository.insertOrUpdateMediaQueueTrackCrossRefs(
                                    mediaQueue.toMediaGroup(),
                                    queueItems.mapIndexed { index, queueItem ->
                                        MediaQueueTrackCrossRef(
                                            mediaGroupType = mediaQueue.mediaGroupType,
                                            groupMediaId = mediaQueue.groupMediaId,
                                            trackId = queueItem.trackRowState.trackId,
                                            trackIndex = index
                                        )
                                    })
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
                    val index =
                        state.value.queueItems.indexOfFirst { it.trackRowState.trackId == action.trackId }
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
                setState {
                    copy(
                        dropdownExpanded = false,
                        chosenQueue = action.newOption,
                    )
                }
            }
        }
    }
}

data class QueueItem(val queuePosition: Int, val trackRowState: TrackRowState)

data class QueueState(
    val queues: List<MediaQueue>,
    val chosenQueue: MediaQueue?,
    val dropdownExpanded: Boolean,
    val mediaGroup: MediaGroup?,
    val queueItems: List<QueueItem>,
    val draggedItem: QueueItem?,
    val draggedItemStartingIndex: Int = -1,
    val draggedItemFinalIndex: Int = -1,
    val nowPlayingTrackIndex: Int,
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
            nowPlayingTrackIndex = -1,
        )
    }
}

sealed class QueueUserAction : UserAction {
    data class ItemDragged(val newIndex: Int) : QueueUserAction()
    data class ItemSelectedForDrag(val index: Int) : QueueUserAction()
    object DragEnded : QueueUserAction()
    data class TrackClicked(val trackId: String) : QueueUserAction()
    object DropdownMenuClicked : QueueUserAction()
    data class DropdownMenuOptionChosen(val newOption: MediaQueue) : QueueUserAction()
}

sealed class QueueUiEvent : UiEvent
