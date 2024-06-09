package com.sebastianvm.musicplayer.util

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.set
import com.navercorp.fixturemonkey.kotlin.size
import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.model.Artist
import com.sebastianvm.musicplayer.model.Genre
import com.sebastianvm.musicplayer.model.Playlist
import com.sebastianvm.musicplayer.model.QueuedTrack
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.model.TrackListMetadata
import com.sebastianvm.musicplayer.model.TrackListWithMetadata
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

    fun genres(size: Int = DEFAULT_LIST_SIZE): List<Genre> {
        return fixtureMonkey.giveMe(size)
    }

    fun track(id: Long = DEFAULT_ID): Track {
        return fixtureMonkey.giveMeBuilder<Track>().set(Track::id, id).build().sample()
    }

    private fun tracks(): List<Track> {
        return fixtureMonkey.giveMe(DEFAULT_LIST_SIZE)
    }

    fun playlists(size: Int = DEFAULT_LIST_SIZE): List<Playlist> {
        return fixtureMonkey.giveMe(size)
    }

    fun playlist(name: String): Playlist {
        return fixtureMonkey.giveMeBuilder<Playlist>().set(Playlist::name, name).build().sample()
    }

    fun playlist(id: Long = DEFAULT_ID): Playlist {
        return fixtureMonkey.giveMeBuilder<Playlist>().set(Playlist::id, id).build().sample()
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
                TrackListWithMetadata(metaData = it, trackList = tracks().toList()),
                TrackListWithMetadata(metaData = it, trackList = listOf()),
            )
        }
    }

    fun trackListSortPreferences(): List<MediaSortPreferences<SortOptions.TrackListSortOptions>> {
        return SortOptions.TrackListSortOptions.entries.flatMap { option ->
            MediaSortOrder.entries.map { order ->
                MediaSortPreferences(sortOption = option, sortOrder = order)
            }
        }
    }

    fun albumSortPreferences(): List<MediaSortPreferences<SortOptions.AlbumListSortOptions>> {
        return SortOptions.AlbumListSortOptions.entries.flatMap { option ->
            MediaSortOrder.entries.map { order ->
                MediaSortPreferences(sortOption = option, sortOrder = order)
            }
        }
    }

    fun queueItemsFixtures(): List<QueuedTrack> {
        return tracks().mapIndexed { index, track ->
            QueuedTrack(track = track, queuePosition = index, queueItemId = track.id)
        }
    }

    private const val DEFAULT_LIST_SIZE = 10
    private const val DEFAULT_ID = 0L
}
