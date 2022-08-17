//package com.sebastianvm.musicplayer.ui.artist
//
//import android.content.ContentUris
//import android.provider.MediaStore
//import android.provider.MediaStore.MediaColumns.ALBUM_ARTIST
//import com.sebastianvm.commons.R
//import com.sebastianvm.musicplayer.database.entities.artistWithAlbums
//import com.sebastianvm.musicplayer.database.entities.fullAlbumInfo
//import com.sebastianvm.musicplayer.repository.album.AlbumRepository
//import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
//import com.sebastianvm.musicplayer.repository.playback.mediaqueue.MediaQueueRepository
//import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
//import com.sebastianvm.musicplayer.ui.components.AlbumRowState
//import com.sebastianvm.musicplayer.util.AlbumType
//import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//
//class ArtistViewModelTest : BaseTest() {
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @get:Rule
//    val mainCoroutineRule = DispatcherSetUpRule()
//
//    private lateinit var albumRepository: AlbumRepository
//    private lateinit var artistRepository: MediaQueueRepository
//
//    @Before
//    fun setUp() {
//        artistRepository = FakeArtistRepository(
//            artistsWithAlbums = listOf(artistWithAlbums {
//                artist { artistName = ARTIST_NAME }
//                albumsForArtistIds { add(ALBUM_ID) }
//                appearsOnForArtistIds { add(APPEARS_ON_ID) }
//            })
//        )
//        albumRepository = FakeAlbumRepository(
//            fullAlbumInfo = listOf(
//                fullAlbumInfo {
//                    album {
//                        albumId = ALBUM_ID
//                        albumName = ALBUM_NAME
//                        year = ALBUM_YEAR
//                        artists = ARTIST_NAME
//                    }
//                    artistIds {
//                        add(ALBUM_ARTIST)
//                    }
//                },
//                fullAlbumInfo {
//                    album {
//                        albumId = APPEARS_ON_ID
//                        albumName = APPEARS_ON_NAME
//                        year = APPEARS_ON_YEAR
//                        artists = APPEARS_ON_ARTIST
//                    }
//                    artistIds {
//                        add(APPEARS_ON_ARTIST)
//                    }
//                })
//        )
//    }
//
//    private fun generateViewModel(): ArtistViewModel {
//        return ArtistViewModel(
//            initialState = ArtistState(
//                artistName = ARTIST_NAME,
//                albumsForArtistItems = listOf(),
//                appearsOnForArtistItems = listOf(),
//            ),
//            albumRepository = albumRepository,
//            artistRepository = artistRepository,
//        )
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `init sets initial state values`() = testScope.runReliableTest {
//        with(generateViewModel()) {
//            assertEquals(
//                listOf(
//                    ArtistScreenItem.SectionHeaderItem(AlbumType.ALBUM, R.string.albums),
//                    ArtistScreenItem.AlbumRowItem(
//                        AlbumRowState(
//                            albumId = ALBUM_ID,
//                            albumName = ALBUM_NAME,
//                            imageUri = ContentUris.withAppendedId(
//                                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                                ALBUM_ID.toLong()
//                            ),
//                            year = ALBUM_YEAR,
//                            artists = ARTIST_NAME
//                        )
//                    )
//                ), state.value.albumsForArtistItems
//            )
//            assertEquals(
//                listOf(
//                    ArtistScreenItem.SectionHeaderItem(AlbumType.APPEARS_ON, R.string.appears_on),
//                    ArtistScreenItem.AlbumRowItem(
//                        AlbumRowState(
//                            albumId = APPEARS_ON_ID,
//                            albumName = APPEARS_ON_NAME,
//                            imageUri = ContentUris.withAppendedId(
//                                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                                APPEARS_ON_ID.toLong()
//                            ),
//                            year = APPEARS_ON_YEAR,
//                            artists = APPEARS_ON_ARTIST
//                        )
//                    )
//                ), state.value.appearsOnForArtistItems
//            )
//
//        }
//    }
//
//    @Test
//    fun `onAlbumClicked adds NavigateToAlbum event`() {
//        with(generateViewModel()) {
//            onAlbumClicked(ALBUM_ID)
//            assertEquals(listOf(ArtistUiEvent.NavigateToAlbum(albumId = ALBUM_ID)), events)
//        }
//    }
//
//    @Test
//    fun `onAlbumOverflowMenuIconClicked adds OpenContextMenu event`() {
//        with(generateViewModel()) {
//            onAlbumOverflowMenuIconClicked(ALBUM_ID)
//            assertEquals(
//                listOf(ArtistUiEvent.OpenContextMenu(albumId = ALBUM_ID)),
//                events
//            )
//
//        }
//    }
//
//    @Test
//    fun `onUpButtonClicked adds NavigateUp event`() {
//        with(generateViewModel()) {
//            onUpButtonClicked()
//            assertEquals(listOf(ArtistUiEvent.NavigateUp), events)
//        }
//    }
//
//    companion object {
//        private const val ARTIST_NAME = "ARTIST_NAME"
//        private const val ALBUM_ID = "0"
//        private const val ALBUM_NAME = "ALBUM_NAME"
//        private const val ALBUM_YEAR = 2000L
//        private const val APPEARS_ON_ID = "1"
//        private const val APPEARS_ON_NAME = "APPEARS_ON_NAME"
//        private const val APPEARS_ON_ARTIST = "APPEARS_ON_ARTIST"
//        private const val APPEARS_ON_YEAR = 1999L
//    }
//}
