package com.sebastianvm.musicplayer.ui.queue

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackInfoRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
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
    private val playbackInfoRepository: PlaybackInfoRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<QueueState, QueueUserAction>(
    initialState
) {

    init {
        viewModelScope.launch {
            playbackInfoRepository.getSavedPlaybackInfo().collect { savedPlaybackInfo ->
                setState {
                    copy(
                        queueItems = savedPlaybackInfo.queuedTracks.map { track ->
                            track.toModelListItemStateWithPosition()
                        },
                        nowPlayingId = savedPlaybackInfo.nowPlayingId
                    )
                }
            }
        }

    }

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.DragEnded -> {
                playbackManager.moveQueueItem(action.initialPosition, action.finalPosition)
                setState { copy(draggedItemFinalIndex = -1) }
            }

            is QueueUserAction.ItemMoved -> {
                val oldIndex = state.draggedItemFinalIndex
                if (oldIndex != action.to) {
                    val items = state.queueItems.toMutableList()
                    val item = items.removeAt(oldIndex)
                    items.add(action.to, item)
                    setState {
                        copy(
                            queueItems = items,
                            draggedItemFinalIndex = action.to
                        )
                    }
                }
            }

            is QueueUserAction.ItemSelectedForDrag -> {
                setState {
                    copy(
                        draggedItemFinalIndex = action.position
                    )
                }
            }

            is QueueUserAction.TrackClicked -> {
                playbackManager.playQueueItem(action.trackIndex)
            }

            is QueueUserAction.TrackOverflowMenuClicked -> {
                // TODO()
            }
        }
    }


}

data class QueueState(
    val mediaGroup: MediaGroup?,
    val queueItems: List<ModelListItemStateWithPosition>,
    val draggedItemFinalIndex: Int = -1,
    val nowPlayingId: Long
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
            nowPlayingId = 0L,
        )
    }
}

sealed interface QueueUserAction : UserAction {
    data class ItemSelectedForDrag(val position: Int) : QueueUserAction
    data class ItemMoved(val to: Int) : QueueUserAction
    data class DragEnded(val initialPosition: Int, val finalPosition: Int) : QueueUserAction
    data class TrackClicked(val trackIndex: Int) : QueueUserAction
    data class TrackOverflowMenuClicked(val trackId: Long) : QueueUserAction
}


