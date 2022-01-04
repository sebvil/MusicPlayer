package com.sebastianvm.musicplayer.ui.queue

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor.COMMAND_MOVE_QUEUE_ITEM
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor.EXTRA_FROM_INDEX
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor.EXTRA_TO_INDEX
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.COMMAND_SEEK_TO_MEDIA_ITEM
import com.sebastianvm.musicplayer.player.EXTRA_MEDIA_INDEX
import com.sebastianvm.musicplayer.player.MEDIA_GROUP
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
import com.sebastianvm.musicplayer.util.extensions.id
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
                collect(mediaQueueRepository.getQueue(mediaGroup)) { queue ->
                    setState {
                        copy(
                            chosenQueue = queue
                        )
                    }
                }
            }
        }

        collect(mediaQueueRepository.getAllQueues()) { queues ->
            setState {
                copy(
                    queues = queues
                )
            }
        }

        collect(musicServiceConnection.nowPlaying) { nowPlaying ->
            setState {
                copy(
                    nowPlayingTrackId = nowPlaying.id ?: ""
                )
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
                    musicServiceConnection.sendCommand(
                        COMMAND_MOVE_QUEUE_ITEM, bundleOf(
                            EXTRA_FROM_INDEX to oldIndex, EXTRA_TO_INDEX to action.newIndex
                        )
                    )
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
            is QueueUserAction.TrackClicked -> {
                with(state.value) {
                    if (chosenQueue !== null) {
                        if (mediaGroup?.mediaId != chosenQueue.groupMediaId || mediaGroup.mediaType != chosenQueue.mediaType) {
                            val extras = Bundle().apply {
                                putParcelable(MEDIA_GROUP, MediaGroup(chosenQueue.mediaType, chosenQueue.groupMediaId))
                            }
                            musicServiceConnection.transportControls.playFromMediaId(action.trackId, extras)
                            return
                        }
                    }
                    val index = state.value.queueItems.indexOfFirst { it.trackId == action.trackId }
                    if (index == -1) return
                    musicServiceConnection.sendCommand(
                        COMMAND_SEEK_TO_MEDIA_ITEM, bundleOf(
                            EXTRA_MEDIA_INDEX to index
                        )
                    )
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
                            action.newOption.mediaType,
                            action.newOption.groupMediaId
                        )
                    ).first()
                        .map { it.toTrackRowState() }
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
    val draggedItemIndex: Int = -1,
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

