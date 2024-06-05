package com.sebastianvm.musicplayer.features.queue

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.queue.QueueRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class QueueItem(
    val modelListItemState: ModelListItemState,
    val position: Int,
    val queueItemId: Long,
)

sealed interface QueueState : State {
    data class Data(
        val nowPlayingItem: QueueItem,
        val queueItems: List<QueueItem>,
    ) : QueueState

    data object Empty : QueueState
    data object Loading : QueueState
}

sealed interface QueueUserAction : UserAction {
    data class DragEnded(val from: Int, val to: Int) : QueueUserAction
    data class TrackClicked(val trackIndex: Int) : QueueUserAction
}

class QueueStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val queueRepository: QueueRepository,
    private val playbackManager: PlaybackManager
) :
    StateHolder<QueueState, QueueUserAction> {

    override val state: StateFlow<QueueState> =
        queueRepository.getQueue().map { queue ->
            queue ?: return@map QueueState.Empty
            QueueState.Data(
                queueItems = queue.nextUp.map { track ->
                    QueueItem(
                        modelListItemState = track.toModelListItemState(),
                        position = track.queuePosition,
                        queueItemId = track.queueItemId
                    )
                },
                nowPlayingItem = queue.nowPlayingTrack.let { track ->
                    QueueItem(
                        modelListItemState = track.toModelListItemState(),
                        position = track.queuePosition,
                        queueItemId = track.queueItemId
                    )
                }
            )
        }.stateIn(
            scope = stateHolderScope,
            started = SharingStarted.Lazily,
            initialValue = QueueState.Loading
        )

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.DragEnded -> {
                queueRepository.moveQueueItem(action.from, action.to)
            }

            is QueueUserAction.TrackClicked -> {
                playbackManager.playQueueItem(action.trackIndex)
            }
        }
    }
}

fun getQueueStateHolder(dependencies: DependencyContainer): QueueStateHolder {
    return QueueStateHolder(
        queueRepository = dependencies.repositoryProvider.queueRepository,
        playbackManager = dependencies.repositoryProvider.playbackManager
    )
}