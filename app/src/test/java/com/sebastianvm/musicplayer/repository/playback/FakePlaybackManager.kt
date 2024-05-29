package com.sebastianvm.musicplayer.repository.playback

import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.PlaybackInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakePlaybackManager(
    val getPlaybackStateValue: MutableSharedFlow<PlaybackState> = MutableSharedFlow(),
    val getSavedPlaybackInfoValue: MutableSharedFlow<PlaybackInfo> = MutableSharedFlow(),
    val playMediaValue: MutableSharedFlow<PlaybackResult> = MutableSharedFlow()
) : PlaybackManager {

    private val _addToQueueInvocations: MutableList<List<Any>> = mutableListOf()

    val addToQueueInvocations: List<List<Any>>
        get() = _addToQueueInvocations

    override fun addToQueue(tracks: List<Track>) {
        _addToQueueInvocations.add(listOf(tracks))
    }

    fun resetAddToQueueInvocations() {
        _addToQueueInvocations.clear()
    }

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

    override fun getSavedPlaybackInfo(): Flow<PlaybackInfo> {
        return getSavedPlaybackInfoValue
    }

    data object ModifySavedPlaybackInfoInvocations

    private val _modifySavedPlaybackInfoInvocations: MutableList<ModifySavedPlaybackInfoInvocations> =
        mutableListOf()

    val modifySavedPlaybackInfoInvocations: List<ModifySavedPlaybackInfoInvocations>
        get() = _modifySavedPlaybackInfoInvocations

    override suspend fun modifySavedPlaybackInfo(newPlaybackInfo: PlaybackInfo) {
        _modifySavedPlaybackInfoInvocations.add(ModifySavedPlaybackInfoInvocations)
    }

    fun resetModifySavedPlaybackInfoInvocations() {
        _modifySavedPlaybackInfoInvocations.clear()
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

    private val _playMediaInvocations: MutableList<List<Any>> = mutableListOf()

    val playMediaInvocations: List<List<Any>>
        get() = _playMediaInvocations

    override suspend fun playMedia(mediaGroup: MediaGroup, initialTrackIndex: Int) {
        _playMediaInvocations.add(listOf(mediaGroup, initialTrackIndex))
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
}
