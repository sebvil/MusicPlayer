package com.sebastianvm.musicplayer.ui.artist

import android.content.ContentUris
import android.provider.MediaStore
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.AlbumWithArtists
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.ArtistWithAlbums
import com.sebastianvm.musicplayer.repository.AlbumRepository
import com.sebastianvm.musicplayer.repository.ArtistRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistViewModel.Companion.ALBUMS
import com.sebastianvm.musicplayer.ui.artist.ArtistViewModel.Companion.APPEARS_ON
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.util.expectUiEvent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class ArtistViewModelTest {

    private lateinit var albumRepository: AlbumRepository
    private lateinit var artistRepository: ArtistRepository

    @Before
    fun setUp() {
        val artist1 = Artist(ARTIST_ID_1, ARTIST_ID_1)
        val artist2 = Artist(ARTIST_ID_2, ARTIST_ID_2)
        val artistAlbum = Album(
            albumId = ALBUM_ID_1,
            albumName = ALBUM_NAME_1,
            numberOfTracks = NUM_TRACKS_1,
            year = YEAR_1
        )

        val artistAppearsOn = Album(
            albumId = ALBUM_ID_2,
            albumName = ALBUM_NAME_2,
            numberOfTracks = NUM_TRACKS_2,
            year = YEAR_2
        )
        artistRepository = mockk {
            every { getArtist(ARTIST_ID_1) } returns flow {
                emit(
                    ArtistWithAlbums(
                        artist1,
                        listOf(artistAlbum),
                        listOf(artistAppearsOn)
                    )
                )
            }
        }
        albumRepository = mockk {
            every { getAlbums(listOf(ALBUM_ID_1)) } returns flow {
                emit(
                    listOf(
                        AlbumWithArtists(artistAlbum, listOf(artist1))
                    )
                )
            }
            every { getAlbums(listOf(ALBUM_ID_2)) } returns flow {
                emit(
                    listOf(
                        AlbumWithArtists(artistAppearsOn, listOf(artist2))
                    )
                )
            }
        }
    }

    private fun generateViewModel(): ArtistViewModel {
        return ArtistViewModel(
            initialState = ArtistState(
                artistHeaderItem = mock(),
                artistId = ARTIST_ID_1,
                albumsForArtistItems = listOf(),
                appearsOnForArtistItems = listOf(),
            ),
            albumRepository = albumRepository,
            artistRepository = artistRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state values`() = runTest {
        with(generateViewModel()) {
            launch {
                assertEquals(
                    HeaderWithImageState(
                        title = DisplayableString.StringValue(ARTIST_ID_1),
                        image = MediaArt(
                            uris = listOf(),
                            contentDescription = DisplayableString.StringValue(""),
                            backupResource = R.drawable.ic_artist,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_artist_image),
                        )
                    ), state.value.artistHeaderItem
                )
                assertEquals(
                    listOf(
                        ArtistScreenItem.SectionHeaderItem(ALBUMS, R.string.albums),
                        ArtistScreenItem.AlbumRowItem(
                            AlbumRowState(
                                ALBUM_ID_1,
                                ALBUM_NAME_1,
                                MediaArt(
                                    uris = listOf(
                                        ContentUris.withAppendedId(
                                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                            ALBUM_ID_1.toLong()
                                        )
                                    ),
                                    contentDescription = DisplayableString.ResourceValue(
                                        value = R.string.album_art_for_album,
                                        arrayOf(ALBUM_NAME_1)
                                    ),
                                    backupResource = R.drawable.ic_album,
                                    backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art)
                                ),
                                YEAR_1,
                                ARTIST_ID_1
                            )
                        )
                    ), state.value.albumsForArtistItems
                )
                assertEquals(
                    listOf(
                        ArtistScreenItem.SectionHeaderItem(APPEARS_ON, R.string.appears_on),
                        ArtistScreenItem.AlbumRowItem(
                            AlbumRowState(
                                ALBUM_ID_2,
                                ALBUM_NAME_2,
                                MediaArt(
                                    uris = listOf(
                                        ContentUris.withAppendedId(
                                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                            ALBUM_ID_2.toLong()
                                        )
                                    ),
                                    contentDescription = DisplayableString.ResourceValue(
                                        value = R.string.album_art_for_album,
                                        arrayOf(ALBUM_NAME_2)
                                    ),
                                    backupResource = R.drawable.ic_album,
                                    backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art)
                                ),
                                YEAR_2,
                                ARTIST_ID_2
                            )
                        )
                    ), state.value.appearsOnForArtistItems
                )
            }
            delay(1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_ID_1, albumId)
            }
            handle(ArtistUserAction.AlbumClicked(ALBUM_ID_1))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumContextButtonClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(ALBUM_ID_1, albumId)
            }
            handle(ArtistUserAction.AlbumContextButtonClicked(ALBUM_ID_1))
        }
    }

    companion object {
        private const val ARTIST_ID_1 = "ARTIST_ID_1"
        private const val ARTIST_ID_2 = "ARTIST_ID_2"
        private const val ALBUM_ID_1 = "1"
        private const val ALBUM_NAME_1 = "ALBUM_NAME_1"
        private const val NUM_TRACKS_1 = 10L
        private const val YEAR_1 = 2020L
        private const val ALBUM_ID_2 = "2"
        private const val ALBUM_NAME_2 = "ALBUM_NAME_2"
        private const val NUM_TRACKS_2 = 15L
        private const val YEAR_2 = 2021L
    }
}