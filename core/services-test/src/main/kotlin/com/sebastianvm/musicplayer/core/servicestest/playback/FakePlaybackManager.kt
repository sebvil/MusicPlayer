package com.sebastianvm.musicplayer.core.servicestest.playback

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.NotPlayingState
import com.sebastianvm.musicplayer.core.model.NowPlayingInfo
import com.sebastianvm.musicplayer.core.model.PlaybackState
import com.sebastianvm.musicplayer.core.model.QueuedTrack
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePlaybackManager : PlaybackManager {

    val getPlaybackStateValue: MutableStateFlow<PlaybackState> = MutableStateFlow(NotPlayingState)

    data object ConnectToServiceInvocations

    private val _connectToServiceInvocations: MutableList<ConnectToServiceInvocations> =
        mutableListOf()
    val connectToServiceInvocations: List<ConnectToServiceInvocations>
        get() = _connectToServiceInvocations

    override fun connectToService() {
        _connectToServiceInvocations.add(ConnectToServiceInvocations)
    }

    fun resetConnectToServiceInvocations() {
        _connectToServiceInvocations.clear()
    }

    data object DisconnectFromServiceInvocations

    private val _disconnectFromServiceInvocations: MutableList<DisconnectFromServiceInvocations> =
        mutableListOf()
    val disconnectFromServiceInvocations: List<DisconnectFromServiceInvocations>
        get() = _disconnectFromServiceInvocations

    override fun disconnectFromService() {
        _disconnectFromServiceInvocations.add(DisconnectFromServiceInvocations)
    }

    fun resetDisconnectFromServiceInvocations() {
        _disconnectFromServiceInvocations.clear()
    }

    override fun getPlaybackState(): Flow<PlaybackState> {
        return getPlaybackStateValue
    }

    private val _nextInvocations: MutableList<List<Any>> = mutableListOf()

    val nextInvocations: List<List<Any>>
        get() = _nextInvocations

    override fun next() {
        _nextInvocations.add(listOf())
    }

    fun resetNextInvocations() {
        _nextInvocations.clear()
    }

    data class PlayMediaArguments(val mediaGroup: MediaGroup, val initialTrackIndex: Int)

    private val _playMediaInvocations: MutableList<PlayMediaArguments> = mutableListOf()

    val playMediaInvocations: List<PlayMediaArguments>
        get() = _playMediaInvocations

    override suspend fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int) {
        _playMediaInvocations.add(PlayMediaArguments(mediaGroup, initialTrackIndex))
    }

    private val _prevInvocations: MutableList<List<Any>> = mutableListOf()

    val prevInvocations: List<List<Any>>
        get() = _prevInvocations

    override fun prev() {
        _prevInvocations.add(listOf())
    }

    fun resetPrevInvocations() {
        _prevInvocations.clear()
    }

    private val _seekToTrackPositionInvocations: MutableList<List<Any>> = mutableListOf()

    val seekToTrackPositionInvocations: List<List<Any>>
        get() = _seekToTrackPositionInvocations

    override fun seekToTrackPosition(position: Long) {
        _seekToTrackPositionInvocations.add(listOf(position))
    }

    fun resetSeekToTrackPositionInvocations() {
        _seekToTrackPositionInvocations.clear()
    }

    private val _togglePlayInvocations: MutableList<List<Any>> = mutableListOf()

    val togglePlayInvocations: List<List<Any>>
        get() = _togglePlayInvocations

    override fun togglePlay() {
        _togglePlayInvocations.add(listOf())
    }

    fun resetTogglePlayInvocations() {
        _togglePlayInvocations.clear()
    }

    val queuedTracks = MutableStateFlow(emptyList<QueuedTrack>())
    val nowPlayingInfo: MutableStateFlow<NowPlayingInfo> =
        MutableStateFlow(NowPlayingInfo(nowPlayingPositionInQueue = 0, lastRecordedPosition = 0))

    override fun moveQueueItem(from: Int, to: Int) {
        queuedTracks.update {
            it.toMutableList()
                .apply { add(to, removeAt(from)) }
                .mapIndexed { index, item -> item.copy(queuePosition = index) }
        }
    }

    override suspend fun addToQueue(trackId: Long) {
        val track = FixtureProvider.track(trackId)
        queuedTracks.update {
            it + QueuedTrack(track = track, queuePosition = it.size, queueItemId = trackId)
        }
    }

    override fun playQueueItem(index: Int) {
        nowPlayingInfo.value =
            NowPlayingInfo(nowPlayingPositionInQueue = index, lastRecordedPosition = 0)
    }

    override fun removeItemsFromQueue(queuePositions: List<Int>) {
        queuedTracks.update {
            it.filterIndexed { index, _ -> index !in queuePositions }
                .mapIndexed { index, queuedTrack -> queuedTrack.copy(queuePosition = index) }
        }
    }
}
