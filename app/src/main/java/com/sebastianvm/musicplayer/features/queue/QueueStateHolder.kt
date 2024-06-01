package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class QueueState(
    val queueItems: List<ModelListItemStateWithPosition>,
    val nowPlayingId: Long
) : State

data class MovedItem(val from: Int, val to: Int)

sealed interface QueueUserAction : UserAction {
    data class ItemSelectedForDrag(val position: Int) : QueueUserAction
    data class ItemMoved(val from: Int, val to: Int) : QueueUserAction
    data object DragEnded : QueueUserAction
    data class TrackClicked(val trackIndex: Int) : QueueUserAction
    data class TrackOverflowMenuClicked(val trackId: Long) : QueueUserAction
}

class QueueStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val playbackManager: PlaybackManager
) :
    StateHolder<QueueState, QueueUserAction> {

    private val movedItem: MutableStateFlow<MovedItem?> = MutableStateFlow(null)

    override val state: StateFlow<QueueState> =
        combine(playbackManager.getSavedPlaybackInfo(), movedItem) { savedPlaybackInfo, movedItem ->
            QueueState(
                queueItems = savedPlaybackInfo.queuedTracks.map { track ->
                    track.toModelListItemStateWithPosition()
                }.toMutableList().apply {
                    if (movedItem != null) {
                        add(movedItem.to, removeAt(movedItem.from))
                    }
                }.toList(),
                nowPlayingId = savedPlaybackInfo.nowPlayingId
            )
        }.stateIn(
            scope = stateHolderScope,
            started = SharingStarted.Lazily,
            initialValue = QueueState(
                queueItems = emptyList(),
                nowPlayingId = 0L
            )
        )

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.DragEnded -> TODO()
            is QueueUserAction.ItemMoved -> {
                movedItem.update { MovedItem(from = action.from, to = action.to) }
            }

            is QueueUserAction.ItemSelectedForDrag -> TODO()
            is QueueUserAction.TrackClicked -> TODO()
            is QueueUserAction.TrackOverflowMenuClicked -> TODO()
        }
    }
}

fun getQueueStateHolder(dependencies: DependencyContainer): QueueStateHolder {
    return QueueStateHolder(playbackManager = dependencies.repositoryProvider.playbackManager)
}
