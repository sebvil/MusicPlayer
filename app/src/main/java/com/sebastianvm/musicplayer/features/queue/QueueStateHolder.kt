package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.model.QueuedTrack
import com.sebastianvm.musicplayer.repository.queue.QueueRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class QueueItem(val trackRow: TrackRow.State, val position: Int, val queueItemId: Long)

sealed interface QueueState : State {
    data class Data(val nowPlayingItem: QueueItem, val queueItems: List<QueueItem>) : QueueState

    data object Empty : QueueState

    data object Loading : QueueState
}

sealed interface QueueUserAction : UserAction {
    data class DragEnded(val from: Int, val to: Int) : QueueUserAction

    data class TrackClicked(val trackIndex: Int) : QueueUserAction
}

class QueueStateHolderServices(val queueRepository: QueueRepository) {
    constructor(dependencies: Dependencies) : this(dependencies.repositoryProvider.queueRepository)
}

class QueueStateHolder(
    services: QueueStateHolderServices,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<QueueState, QueueUserAction> {

    private val queueRepository = services.queueRepository

    override val state: StateFlow<QueueState> =
        queueRepository
            .getQueue()
            .map { queue ->
                queue ?: return@map QueueState.Empty
                QueueState.Data(
                    queueItems = queue.nextUp.map { track -> track.toQueueItem() },
                    nowPlayingItem = queue.nowPlayingTrack.toQueueItem(),
                )
            }
            .stateIn(
                scope = stateHolderScope,
                started = SharingStarted.Lazily,
                initialValue = QueueState.Loading,
            )

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.DragEnded -> {
                queueRepository.moveQueueItem(action.from, action.to)
            }
            is QueueUserAction.TrackClicked -> {
                queueRepository.playQueueItem(action.trackIndex)
            }
        }
    }
}

fun QueuedTrack.toQueueItem(): QueueItem {
    return QueueItem(
        trackRow =
            TrackRow.State(id = id, trackName = trackName, artists = artists.ifEmpty { null }),
        position = queuePosition,
        queueItemId = queueItemId,
    )
}
