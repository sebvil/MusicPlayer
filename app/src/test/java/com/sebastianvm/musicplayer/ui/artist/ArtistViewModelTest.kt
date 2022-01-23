package com.sebastianvm.musicplayer.ui.artist

import android.content.ContentUris
import android.net.Uri
import com.sebastianvm.commons.R
import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistViewModel.Companion.ALBUMS
import com.sebastianvm.musicplayer.ui.artist.ArtistViewModel.Companion.APPEARS_ON
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.expectUiEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ArtistViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    private val albumRepository: AlbumRepository = FakeAlbumRepository()
    private val artistRepository: ArtistRepository = FakeArtistRepository()

    private lateinit var defaultUri: Uri
    private lateinit var secondaryUri: Uri

    @Before
    fun setUp() {
        mockkStatic(ContentUris::class)
        defaultUri = mockk()
        secondaryUri = mockk()
        every {
            ContentUris.withAppendedId(any(), AlbumBuilder.DEFAULT_ALBUM_ID.toLong())
        } returns defaultUri
        every {
            ContentUris.withAppendedId(any(), AlbumBuilder.SECONDARY_ALBUM_ID.toLong())
        } returns secondaryUri
    }

    private fun generateViewModel(): ArtistViewModel {
        return ArtistViewModel(
            initialState = ArtistState(
                artistName = ArtistBuilder.DEFAULT_ARTIST_NAME,
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
            delay(1)
            assertEquals(
                listOf(
                    ArtistScreenItem.SectionHeaderItem(ALBUMS, R.string.albums),
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = AlbumBuilder.DEFAULT_ALBUM_ID,
                            albumName = AlbumBuilder.DEFAULT_ALBUM_NAME,
                            imageUri = defaultUri,
                            year = AlbumBuilder.DEFAULT_YEAR,
                            artists = ArtistBuilder.DEFAULT_ARTIST_NAME
                        )
                    )
                ), state.value.albumsForArtistItems
            )
            assertEquals(
                listOf(
                    ArtistScreenItem.SectionHeaderItem(APPEARS_ON, R.string.appears_on),
                    ArtistScreenItem.AlbumRowItem(
                        AlbumRowState(
                            albumId = AlbumBuilder.SECONDARY_ALBUM_ID,
                            albumName = AlbumBuilder.SECONDARY_ALBUM_NAME,
                            imageUri = secondaryUri,
                            year = AlbumBuilder.SECONDARY_YEAR,
                            artists = ArtistBuilder.SECONDARY_ARTIST_NAME
                        )
                    )
                ), state.value.appearsOnForArtistItems
            )

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumId)
            }
            handle(ArtistUserAction.AlbumClicked(AlbumBuilder.DEFAULT_ALBUM_ID))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumContextButtonClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumId)
            }
            handle(ArtistUserAction.AlbumContextButtonClicked(AlbumBuilder.DEFAULT_ALBUM_ID))
        }
    }
}
