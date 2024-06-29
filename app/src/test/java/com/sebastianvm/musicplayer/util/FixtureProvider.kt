package com.sebastianvm.musicplayer.util

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary
import com.navercorp.fixturemonkey.api.arbitrary.JavaTypeArbitraryGeneratorSet
import com.navercorp.fixturemonkey.api.generator.ArbitraryGeneratorContext
import com.navercorp.fixturemonkey.kotest.KotestJavaArbitraryGeneratorSet
import com.navercorp.fixturemonkey.kotest.KotestPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.navercorp.fixturemonkey.kotlin.set
import com.navercorp.fixturemonkey.kotlin.size
import com.sebastianvm.musicplayer.core.model.Album
import com.sebastianvm.musicplayer.core.model.Artist
import com.sebastianvm.musicplayer.core.model.Genre
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.Playlist
import com.sebastianvm.musicplayer.core.model.QueuedTrack
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.model.Track
import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import kotlin.time.Duration.Companion.seconds

object FixtureProvider {

    private val fixtureMonkey =
        FixtureMonkey.builder()
            .plugin(KotlinPlugin())
            .plugin(KotestPlugin())
            .plugin { optionsBuilder ->
                optionsBuilder?.javaTypeArbitraryGeneratorSet {
                    object : JavaTypeArbitraryGeneratorSet by KotestJavaArbitraryGeneratorSet(it) {
                        override fun strings(
                            context: ArbitraryGeneratorContext
                        ): CombinableArbitrary<String> {
                            val stringConstraint = it.generateStringConstraint(context)

                            return CombinableArbitrary.from {
                                if (stringConstraint != null) {
                                    val minSize = stringConstraint.minSize?.toInt() ?: 3
                                    val maxSize = stringConstraint.maxSize?.toInt() ?: 10
                                    Arb.string(
                                            minSize = minSize,
                                            maxSize = maxSize,
                                            codepoints = Codepoint.az(),
                                        )
                                        .single()
                                } else {
                                    Arb.string(
                                            minSize = 3,
                                            maxSize = 10,
                                            codepoints = Codepoint.az(),
                                        )
                                        .single()
                                }
                            }
                        }
                    }
                }
            }
            .build()

    fun album(id: Long = fixtureMonkey.giveMeOne<Long>(), artistCount: Int = 1): Album {
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

    fun genre(
        id: Long = fixtureMonkey.giveMeOne<Long>(),
        name: String = fixtureMonkey.giveMeOne(),
    ): Genre {
        return fixtureMonkey
            .giveMeBuilder<Genre>()
            .set(Genre::id, id)
            .set(Genre::name, name)
            .build()
            .sample()
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
            .also {
                val ids = it.map { track -> track.id }
                check(ids.size == ids.toSet().size)
            }
    }

    fun playlists(size: Int = DEFAULT_LIST_SIZE): List<Playlist> {
        return fixtureMonkey.giveMe(size)
    }

    fun playlist(
        id: Long = fixtureMonkey.giveMeOne<Long>(),
        name: String = fixtureMonkey.giveMeOne<String>(),
        trackCount: Int = fixtureMonkey.giveMeOne<Int>().coerceAtMost(5),
    ): Playlist {
        return fixtureMonkey
            .giveMeBuilder<Playlist>()
            .set(Playlist::id, id)
            .set(Playlist::name, name)
            .size(Playlist::tracks, trackCount)
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

    fun trackListSortPreferences(): List<MediaSortPreferences<SortOptions.TrackListSortOption>> {
        return SortOptions.forTracks.flatMap { option ->
            MediaSortOrder.entries.map { order ->
                MediaSortPreferences(sortOption = option, sortOrder = order)
            }
        }
    }

    fun albumSortPreferences(): List<MediaSortPreferences<SortOptions.AlbumListSortOption>> {
        return SortOptions.forAlbums.flatMap { option ->
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
