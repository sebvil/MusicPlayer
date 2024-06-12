package com.sebastianvm.musicplayer.features.queue

import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.model.QueuedTrack
import com.sebastianvm.musicplayer.repository.queue.QueueRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.extensions.collectValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

data class QueueItem(val trackRow: TrackRow.State, val position: Int, val queueItemId: Long)

sealed interface QueueState : State {
    data class Data(val nowPlayingItem: QueueItem, val queueItems: List<QueueItem>) : QueueState

    data object Loading : QueueState
}

sealed interface QueueUserAction : UserAction {
    data class DragEnded(val from: Int, val to: Int) : QueueUserAction

    data class TrackClicked(val trackIndex: Int) : QueueUserAction

    data class RemoveItemsFromQueue(val queuePositions: List<Int>) : QueueUserAction
}

class QueueStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val queueRepository: QueueRepository,
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) : StateHolder<QueueState, QueueUserAction> {

    override val state: StateFlow<QueueState> =
        stateHolderScope.launchMolecule(recompositionMode) {
            val queue = queueRepository.getQueue().collectValue(initial = null)
            if (queue == null) {
                QueueState.Loading
            } else {
                QueueState.Data(
                    queueItems = queue.nextUp.map { track -> track.toQueueItem() },
                    nowPlayingItem = queue.nowPlayingTrack.toQueueItem(),
                )
            }
        }

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.DragEnded -> {
                queueRepository.moveQueueItem(action.from, action.to)
            }
            is QueueUserAction.TrackClicked -> {
                queueRepository.playQueueItem(action.trackIndex)
            }
            is QueueUserAction.RemoveItemsFromQueue -> {
                queueRepository.removeItemsFromQueue(action.queuePositions)
            }
        }
    }
}

fun QueuedTrack.toQueueItem(): QueueItem {
    return QueueItem(
        trackRow =
            TrackRow.State(
                id = track.id,
                trackName = track.name,
                artists = track.artists.joinToString { it.name }.ifEmpty { null },
            ),
        position = queuePosition,
        queueItemId = queueItemId,
    )
}
