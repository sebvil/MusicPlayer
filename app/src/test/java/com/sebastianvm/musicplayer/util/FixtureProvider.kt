@file:Suppress("unused")

package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlin.time.Duration.Companion.seconds

object FixtureProvider {

    fun playbackStateFixtures(): List<TrackPlayingState> {
        return listOf(
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
                isPlaying = true,
                currentTrackProgress = 125.seconds
            ),
        )
    }

    private fun longList(): List<Long> = listOf(0, 1, 2, 3)
    private fun stringList() = listOf("", "Hello, World!")

    fun trackFixtures(): List<Track> {
        return longList().flatMap { long ->
            stringList().map { string ->
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

    fun trackListWithMetadataFixtures(): List<TrackListWithMetadata> {
        val metadataList = listOf(
            TrackListMetadata(trackListName = "Track list", mediaArtImageState = null),
            TrackListMetadata(
                trackListName = "Track list",
                mediaArtImageState = MediaArtImageState("", Icons.Album)
            ),
            null
        )
        return metadataList.flatMap {
            listOf(
                TrackListWithMetadata(metaData = it, trackList = trackFixtures().toList()),
                TrackListWithMetadata(metaData = it, trackList = listOf())
            )
        }
    }

    private fun trackListSortOptions(): List<SortOptions.TrackListSortOptions> =
        SortOptions.TrackListSortOptions.entries

    fun sortOrders() = MediaSortOrder.entries

    fun trackListSortPreferences(): List<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return trackListSortOptions().flatMap { option ->
            sortOrders().map { order ->
                MediaSortPreferences(sortOption = option, sortOrder = order)
            }
        }
    }

    fun albumSortPreferences(): List<MediaSortPreferences<SortOptions.AlbumListSortOptions>> {
        return SortOptions.AlbumListSortOptions.entries.flatMap { option ->
            sortOrders().map { order ->
                MediaSortPreferences(sortOption = option, sortOrder = order)
            }
        }
    }

    fun albumFixtures(): List<Album> {
        return longList().flatMap { long ->
            stringList().map { string ->
                Album(
                    id = long,
                    albumName = string,
                    artists = string,
                    year = long,
                    imageUri = string
                )
            }
        }
    }

    fun artistFixtures(): List<Artist> {
        return longList().flatMap { long ->
            stringList().map { string ->
                Artist(
                    id = long,
                    artistName = string
                )
            }
        }
    }

    fun genreFixtures(): List<Genre> {
        return longList().flatMap { long ->
            stringList().map { string ->
                Genre(
                    id = long,
                    genreName = string
                )
            }
        }
    }
}
