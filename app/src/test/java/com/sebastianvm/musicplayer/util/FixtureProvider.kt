package com.sebastianvm.musicplayer.util

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.set
import com.navercorp.fixturemonkey.kotlin.size
import com.sebastianvm.musicplayer.model.AlbumWithArtists
import com.sebastianvm.musicplayer.model.Artist
import com.sebastianvm.musicplayer.model.Genre
import com.sebastianvm.musicplayer.model.Playlist
import com.sebastianvm.musicplayer.model.QueuedTrack
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import kotlin.time.Duration.Companion.seconds

object FixtureProvider {

    private val fixtureMonkey = FixtureMonkey.builder().plugin(KotlinPlugin()).build()

    fun album(id: Long = fixtureMonkey.giveMeOne<Long>(), artistCount: Int = 1): AlbumWithArtists {
        return fixtureMonkey
            .giveMeBuilder<AlbumWithArtists>()
            .set(AlbumWithArtists::id, id)
            .size(AlbumWithArtists::artists, artistCount)
            .build()
            .sample()
    }

    fun albums(size: Int = DEFAULT_LIST_SIZE): List<AlbumWithArtists> {
        return fixtureMonkey.giveMe(size)
    }

    fun artist(
        id: Long = fixtureMonkey.giveMeOne<Long>(),
        albumCount: Int = 1,
        appearsOnCount: Int = 1,
    ): Artist {
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

    fun genre(id: Long = fixtureMonkey.giveMeOne<Long>()): Genre {
        return fixtureMonkey.giveMeBuilder<Genre>().set(Genre::id, id).build().sample()
    }

    fun genres(size: Int = DEFAULT_LIST_SIZE): List<Genre> {
        return fixtureMonkey.giveMe(size)
    }

    fun track(
        id: Long = fixtureMonkey.giveMeOne<Long>(),
        artistCount: Int = 1,
        albumId: Long = fixtureMonkey.giveMeOne<Long>(),
    ): Track {
        return fixtureMonkey
            .giveMeBuilder<Track>()
            .set(Track::id, id)
            .size(Track::artists, artistCount)
            .set(Track::albumId, albumId)
            .build()
            .sample()
    }

    fun tracks(
        size: Int = DEFAULT_LIST_SIZE,
        albumId: Long = fixtureMonkey.giveMeOne<Long>(),
    ): List<Track> {
        return fixtureMonkey
            .giveMeBuilder<Track>()
            .set(Track::albumId, albumId)
            .build()
            .list()
            .ofSize(size)
            .uniqueElements { it.id }
            .sample()
    }

    fun playlists(size: Int = DEFAULT_LIST_SIZE): List<Playlist> {
        return fixtureMonkey.giveMe(size)
    }

    fun playlist(name: String): Playlist {
        return fixtureMonkey.giveMeBuilder<Playlist>().set(Playlist::name, name).build().sample()
    }

    fun playlist(
        id: Long = fixtureMonkey.giveMeOne<Long>(),
        name: String = fixtureMonkey.giveMeOne<String>(),
    ): Playlist {
        return fixtureMonkey
            .giveMeBuilder<Playlist>()
            .set(Playlist::id, id)
            .set(Playlist::name, name)
            .build()
            .sample()
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
}
