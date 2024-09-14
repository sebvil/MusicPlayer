package com.sebastianvm.musicplayer.features.queue

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.UriUtils
import com.sebastianvm.musicplayer.core.data.queue.QueueRepository
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.QueuedTrack
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class QueueItem(val trackRow: TrackRow.State, val position: Int, val queueItemId: Long)

sealed interface QueueState : State {
    data class Data(
        val nowPlayingItem: QueueItem,
        val nowPlayingItemArtworkUri: String,
        val queueItems: List<QueueItem>,
    ) : QueueState

    data object Loading : QueueState
}

sealed interface QueueUserAction : UserAction {
    data class DragEnded(val from: Int, val to: Int) : QueueUserAction

    data class TrackClicked(val trackIndex: Int) : QueueUserAction

    data class RemoveItemsFromQueue(val queuePositions: List<Int>) : QueueUserAction
}

class QueueViewModel(
    vmScope: CoroutineScope = getViewModelScope(),
    queueRepository: QueueRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<QueueState, QueueUserAction>(viewModelScope = vmScope) {

    override val state: StateFlow<QueueState> =
        queueRepository
            .getQueue()
            .map { queue ->
                QueueState.Data(
                    queueItems = queue.nextUp.map { track -> track.toQueueItem() },
                    nowPlayingItem = queue.nowPlayingTrack.toQueueItem(),
                    nowPlayingItemArtworkUri =
                        UriUtils.getAlbumUriString(queue.nowPlayingTrack.track.albumId),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = QueueState.Loading,
            )

    override fun handle(action: QueueUserAction) {
        when (action) {
            is QueueUserAction.DragEnded -> {
                playbackManager.moveQueueItem(action.from, action.to)
            }
            is QueueUserAction.TrackClicked -> {
                playbackManager.playQueueItem(action.trackIndex)
            }
            is QueueUserAction.RemoveItemsFromQueue -> {
                playbackManager.removeItemsFromQueue(action.queuePositions)
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
