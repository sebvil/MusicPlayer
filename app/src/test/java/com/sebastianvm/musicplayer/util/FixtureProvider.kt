package com.sebastianvm.musicplayer.util

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.set
import com.navercorp.fixturemonkey.kotlin.size
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.QueuedTrack
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.model.Artist
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlin.time.Duration.Companion.seconds

object FixtureProvider {

    private val fixtureMonkey = FixtureMonkey.builder().plugin(KotlinPlugin()).build()

    fun album(id: Long = DEFAULT_ID, artistCount: Int = 1): Album {
        return fixtureMonkey
            .giveMeBuilder<Album>()
            .set(Album::id, id)
            .size(Album::artists, artistCount)
            .build()
            .sample()
    }

    fun albums(size: Int = DEFAULT_LIST_SIZE): List<Album> {
        return fixtureMonkey.giveMe(size)
    }

    fun artist(id: Long = DEFAULT_ID, albumCount: Int = 1, appearsOnCount: Int = 1): Artist {
        return fixtureMonkey
            .giveMeBuilder<Artist>()
            .set(Artist::id, id)
            .size(Artist::albums, albumCount)
            .size(Artist::appearsOn, appearsOnCount)
            .build()
            .sample()
    }

    fun artists(size: Int = DEFAULT_LIST_SIZE): List<Artist> {
        return fixtureMonkey.giveMe(size)
    }

    fun playbackStateFixtures(): List<TrackPlayingState> {
        return listOf(
            TrackPlayingState(
                trackInfo =
                    TrackInfo(title = "", artists = "", artworkUri = "", trackLength = 0.seconds),
                isPlaying = false,
                currentTrackProgress = 0.seconds,
            ),
            TrackPlayingState(
                trackInfo =
                    TrackInfo(
                        title = "La Promesa",
                        artists = "Melendi",
                        artworkUri = "path/to/image",
                        trackLength = 250.seconds,
                    ),
                isPlaying = true,
                currentTrackProgress = 125.seconds,
            ),
        )
    }

    private fun longList(): List<Long> = listOf(0, 1, 2, 3)

    private fun stringList() = listOf("", "Hello, World!")

    fun trackFixtures(): List<Track> {
        return longList().flatMap { long ->
            stringList().map { string ->
                Track(id = long, name = string, albumId = long, artists = emptyList())
            }
        }
    }

    fun trackListWithMetadataFixtures(): List<TrackListWithMetadata> {
        val metadataList =
            listOf(
                TrackListMetadata(trackListName = "Track list", mediaArtImageState = null),
                TrackListMetadata(
                    trackListName = "Track list",
                    mediaArtImageState = MediaArtImageState("", Icons.Album),
                ),
                null,
            )
        return metadataList.flatMap {
            listOf(
                TrackListWithMetadata(metaData = it, trackList = trackFixtures().toList()),
                TrackListWithMetadata(metaData = it, trackList = listOf()),
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

    fun albumFixtures(size: Int = DEFAULT_LIST_SIZE): List<Album> {
        return fixtureMonkey.giveMe(size)
    }

    fun genreFixtures(): List<Genre> {
        return longList().flatMap { long ->
            stringList().map { string -> Genre(id = long, genreName = string) }
        }
    }

    fun queueItemsFixtures(): List<QueuedTrack> {
        return trackFixtures().mapIndexed { index, track ->
            QueuedTrack(
                id = track.id,
                trackName = track.name,
                artists = track.artists.joinToString(),
                queuePosition = index,
                queueItemId = track.id,
            )
        }
    }

    private const val DEFAULT_LIST_SIZE = 10
    private const val DEFAULT_ID = 0L
}
