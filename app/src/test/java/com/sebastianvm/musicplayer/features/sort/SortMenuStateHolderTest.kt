package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class SortMenuStateHolderTest :
    FreeSpec({
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository

        beforeTest { sortPreferencesRepositoryDep = FakeSortPreferencesRepository() }

        fun TestScope.getSubject(
            listType: SortableListType = SortableListType.AllTracks
        ): SortMenuStateHolder {
            return SortMenuStateHolder(
                arguments = SortMenuArguments(listType),
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                stateHolderScope = this,
            )
        }

        "init sets state and subscribes to changes in preferences" -
            {
                "for all tracks" {
                    sortPreferencesRepositoryDep.allTracksSortPreferences.value =
                        MediaSortPreferences(
                            sortOption = SortOptions.TrackListSortOptions.TRACK,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                    val subject = getSubject(listType = SortableListType.AllTracks)
                    testStateHolderState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.TrackListSortOptions.entries,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.TrackListSortOptions.entries,
                                selectedSort = SortOptions.TrackListSortOptions.TRACK,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.allTracksSortPreferences.value =
                            MediaSortPreferences(
                                sortOption = SortOptions.TrackListSortOptions.ARTIST,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )

                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.TrackListSortOptions.entries,
                                selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }

                "for genre" {
                    sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
                        mapOf(
                            GENRE_ID to
                                MediaSortPreferences(
                                    sortOption = SortOptions.TrackListSortOptions.TRACK,
                                    sortOrder = MediaSortOrder.ASCENDING,
                                )
                        )

                    val subject = getSubject(listType = SortableListType.Genre(GENRE_ID))
                    testStateHolderState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.TrackListSortOptions.entries,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.TrackListSortOptions.entries,
                                selectedSort = SortOptions.TrackListSortOptions.TRACK,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
                            mapOf(
                                GENRE_ID to
                                    MediaSortPreferences(
                                        sortOption = SortOptions.TrackListSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            )

                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.TrackListSortOptions.entries,
                                selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }

                "for playlist" {
                    sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                        mapOf(
                            PLAYLIST_ID to
                                MediaSortPreferences(
                                    sortOption = SortOptions.PlaylistSortOptions.TRACK,
                                    sortOrder = MediaSortOrder.ASCENDING,
                                )
                        )

                    val subject = getSubject(listType = SortableListType.Playlist(PLAYLIST_ID))
                    testStateHolderState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.PlaylistSortOptions.entries,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.PlaylistSortOptions.entries,
                                selectedSort = SortOptions.PlaylistSortOptions.TRACK,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                            mapOf(
                                PLAYLIST_ID to
                                    MediaSortPreferences(
                                        sortOption = SortOptions.PlaylistSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            )

                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.PlaylistSortOptions.entries,
                                selectedSort = SortOptions.PlaylistSortOptions.ARTIST,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }

                "for albums" {
                    sortPreferencesRepositoryDep.albumListSortPreferences.value =
                        MediaSortPreferences(
                            sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )

                    val subject = getSubject(listType = SortableListType.Albums)
                    testStateHolderState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.AlbumListSortOptions.entries,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.AlbumListSortOptions.entries,
                                selectedSort = SortOptions.AlbumListSortOptions.ALBUM,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.albumListSortPreferences.value =
                            MediaSortPreferences(
                                sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.AlbumListSortOptions.entries,
                                selectedSort = SortOptions.AlbumListSortOptions.ARTIST,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }
            }

        "handle" -
            {
                "MediaSortOptionClicked updates preferences" -
                    {
                        "for all tracks" {
                            val subject = getSubject(listType = SortableListType.AllTracks)
                            testStateHolderState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.TrackListSortOptions.ARTIST,
                                        selectedSort = SortOptions.TrackListSortOptions.TRACK,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.TrackListSortOptions.entries,
                                        selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.TrackListSortOptions.ARTIST,
                                        selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.TrackListSortOptions.entries,
                                        selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            }
                        }

                        "for genre" {
                            val subject = getSubject(listType = SortableListType.Genre(GENRE_ID))
                            testStateHolderState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.TrackListSortOptions.ARTIST,
                                        selectedSort = SortOptions.TrackListSortOptions.TRACK,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.TrackListSortOptions.entries,
                                        selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.TrackListSortOptions.ARTIST,
                                        selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.TrackListSortOptions.entries,
                                        selectedSort = SortOptions.TrackListSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            }
                        }

                        "for playlist" {
                            val subject =
                                getSubject(listType = SortableListType.Playlist(PLAYLIST_ID))
                            testStateHolderState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.PlaylistSortOptions.ARTIST,
                                        selectedSort = SortOptions.PlaylistSortOptions.TRACK,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.PlaylistSortOptions.entries,
                                        selectedSort = SortOptions.PlaylistSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.PlaylistSortOptions.ARTIST,
                                        selectedSort = SortOptions.PlaylistSortOptions.ARTIST,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.PlaylistSortOptions.entries,
                                        selectedSort = SortOptions.PlaylistSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            }
                        }

                        "for albums" {
                            val subject = getSubject(listType = SortableListType.Albums)
                            testStateHolderState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.AlbumListSortOptions.ARTIST,
                                        selectedSort = SortOptions.AlbumListSortOptions.ALBUM,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.AlbumListSortOptions.entries,
                                        selectedSort = SortOptions.AlbumListSortOptions.ARTIST,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.AlbumListSortOptions.ARTIST,
                                        selectedSort = SortOptions.AlbumListSortOptions.ARTIST,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                            }
                        }
                    }
            }
    }) {
    companion object {
        private const val GENRE_ID = 1L
        private const val PLAYLIST_ID = 1L
    }
}
