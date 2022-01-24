package com.sebastianvm.musicplayer.ui.queue

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor.COMMAND_MOVE_QUEUE_ITEM
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor.EXTRA_FROM_INDEX
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor.EXTRA_TO_INDEX
import com.sebastianvm.musicplayer.database.entities.MediaQueue
import com.sebastianvm.musicplayer.database.entities.MediaQueueTrackCrossRef
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.COMMAND_SEEK_TO_MEDIA_ITEM
import com.sebastianvm.musicplayer.repository.playback.EXTRA_MEDIA_INDEX
import com.sebastianvm.musicplayer.repository.playback.MEDIA_GROUP
import com.sebastianvm.musicplayer.repository.playback.PlaybackServiceRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
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
    private val playbackServiceRepository: PlaybackServiceRepository,
) : BaseViewModel<QueueUserAction, QueueUiEvent, QueueState>(initialState) {

    init {
        collect(playbackServiceRepository.currentQueueId) { mediaGroup ->
            setState {
                copy(
                    mediaGroup = mediaGroup
                )
            }
            mediaGroup?.also {
                val tracks = tracksRepository.getTracksForQueue(it).first()
                val mediaQueue = mediaQueueRepository.getQueue(mediaGroup).first()
                setState {
                    copy(
                        chosenQueue = mediaQueue,
                        queueItems = tracks.map { track -> track.toTrackRowState(includeTrackNumber = false) }
                    )
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

        collect(playbackServiceRepository.nowPlaying) { nowPlaying ->
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
                    if (state.value.chosenQueue?.groupMediaId == state.value.mediaGroup?.mediaId
                        && state.value.chosenQueue?.mediaType == state.value.mediaGroup?.mediaType
                    ) {
                        playbackServiceRepository.sendCommand(
                            COMMAND_MOVE_QUEUE_ITEM, bundleOf(
                                EXTRA_FROM_INDEX to oldIndex, EXTRA_TO_INDEX to action.newIndex
                            )
                        )
                    }
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

                        chosenQueue?.also { mediaQueue ->
                            viewModelScope.launch {
                                mediaQueueRepository.insertOrUpdateMediaQueueTrackCrossRefs(
                                    queueItems.mapIndexed { index, trackRowState ->
                                        MediaQueueTrackCrossRef(
                                            mediaType = mediaQueue.mediaType,
                                            groupMediaId = mediaQueue.groupMediaId,
                                            trackId = trackRowState.trackId,
                                            trackIndex = index
                                        )
                                    })

                                val tracks = tracksRepository.getTracksForQueue(mediaQueue.toMediaGroup()).first()
                                setState {
                                    copy(
                                        chosenQueue = mediaQueue,
                                        queueItems = tracks.map { track -> track.toTrackRowState(includeTrackNumber = false) }
                                    )
                                }
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
                                putParcelable(
                                    MEDIA_GROUP,
                                    MediaGroup(chosenQueue.mediaType, chosenQueue.groupMediaId)
                                )
                            }
                            playbackServiceRepository.transportControls.playFromMediaId(
                                action.trackId,
                                extras
                            )
                            return
                        }
                    }
                    val index = state.value.queueItems.indexOfFirst { it.trackId == action.trackId }
                    if (index == -1) return
                    playbackServiceRepository.sendCommand(
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
