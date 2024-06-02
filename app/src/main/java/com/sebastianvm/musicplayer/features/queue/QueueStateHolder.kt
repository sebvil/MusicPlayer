package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.repository.queue.QueueRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemStateWithPosition
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface QueueState : State {
    data class Data(
        val nowPlayingItem: ModelListItemStateWithPosition,
        val queueItems: List<ModelListItemStateWithPosition>,
    ) : QueueState

    data object Loading : QueueState
}

sealed interface QueueUserAction : UserAction {
    data class ItemSelectedForDrag(val position: Int) : QueueUserAction
    data class ItemMoved(val from: Int, val to: Int) : QueueUserAction
    data object DragEnded : QueueUserAction
    data class TrackClicked(val trackIndex: Int) : QueueUserAction
    data class TrackOverflowMenuClicked(val trackId: Long) : QueueUserAction
}

class QueueStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    queueRepository: QueueRepository,
) :
    StateHolder<QueueState, QueueUserAction> {

    override val state: StateFlow<QueueState> =
        queueRepository.getQueue().map { queue ->
            QueueState.Data(
                queueItems = queue.nextUp.map { track ->
                    track.toModelListItemStateWithPosition()
                },
                nowPlayingItem = queue.nowPlayingTrack.toModelListItemStateWithPosition()
            )
        }.stateIn(
            scope = stateHolderScope,
            started = SharingStarted.Lazily,
            initialValue = QueueState.Loading
        )

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.DragEnded -> TODO()
            is QueueUserAction.ItemMoved -> {
            }

            is QueueUserAction.ItemSelectedForDrag -> TODO()
            is QueueUserAction.TrackClicked -> TODO()
            is QueueUserAction.TrackOverflowMenuClicked -> TODO()
        }
    }
}

fun getQueueStateHolder(dependencies: DependencyContainer): QueueStateHolder {
    return QueueStateHolder(queueRepository = dependencies.repositoryProvider.queueRepository)
}
