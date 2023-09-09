package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import java.util.stream.Stream
import kotlin.time.Duration.Companion.seconds

object FixtureProvider {

    @JvmStatic
    fun playbackStateFixtures(): Stream<TrackPlayingState> {
        return Stream.of(
            TrackPlayingState(
                trackInfo = TrackInfo(
                    title = "",
                    artists = "",
                    artworkUri = "",
                    trackLength = 0.seconds
                ),
                isPlaying = false,
                currentTrackProgress = 0.seconds
            ),
            TrackPlayingState(
                trackInfo = TrackInfo(
                    title = "La Promesa",
                    artists = "Melendi",
                    artworkUri = "path/to/image",
                    trackLength = 250.seconds
                ),
                isPlaying = false,
                currentTrackProgress = 125.seconds
            ),
        )
    }
}
