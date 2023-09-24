@file:Suppress("unused")

package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.icons.Album
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
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

    private fun longStream(): Stream<Long> = Stream.of(0, 1, 2, 3)
    private fun stringStream() = Stream.of("", "Hello, World!")

    private fun trackFixtures(): Stream<Track> {
        return longStream().flatMap { long ->
            stringStream().map { string ->
                Track(
                    id = long,
                    trackName = string,
                    trackNumber = long,
                    trackDurationMs = long,
                    albumName = string,
                    albumId = long,
                    artists = string,
                    path = string
                )
            }
        }
    }

    @JvmStatic
    fun trackListWithMetadataFixtures(): Stream<TrackListWithMetadata> {
        val metadataStream = Stream.of(
            TrackListMetadata(trackListName = "Track list", mediaArtImageState = null),
            TrackListMetadata(
                trackListName = "Track list",
                mediaArtImageState = MediaArtImageState("", Icons.Album)
            ),
            null
        )
        return metadataStream.flatMap {
            Stream.of(
                TrackListWithMetadata(metaData = it, trackList = trackFixtures().toList()),
                TrackListWithMetadata(metaData = it, trackList = listOf())
            )
        }
    }

    private fun trackListSortOptions(): Stream<SortOptions.TrackListSortOptions> =
        SortOptions.TrackListSortOptions.entries.stream()

    private fun sortOrders() = MediaSortOrder.entries.stream()

    @JvmStatic
    fun trackListSortPreferences(): Stream<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return trackListSortOptions().flatMap { option ->
            sortOrders().map { order ->
                MediaSortPreferences(sortOption = option, sortOrder = order)
            }
        }
    }
}
