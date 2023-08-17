package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.ui.player.MinutesSecondsTime
import java.util.stream.Stream

object FixtureProvider {

    @JvmStatic
    fun playbackStateFixtures(): Stream<TrackPlayingState> {
        return Stream.of(
            TrackPlayingState(
                trackInfo = TrackInfo(
                    title = "",
                    artists = "",
                    artworkUri = "",
                    trackLength = MinutesSecondsTime.fromMs(0)
                ),
                isPlaying = false,
                currentPlayTime = MinutesSecondsTime.fromMs(0)
            ),
            TrackPlayingState(
                trackInfo = TrackInfo(
                    title = "La Promesa",
                    artists = "Melendi",
                    artworkUri = "path/to/image",
                    trackLength = MinutesSecondsTime.fromMs(250)
                ),
                isPlaying = false,
                currentPlayTime = MinutesSecondsTime.fromMs(125)
            ),
        )
    }
}
