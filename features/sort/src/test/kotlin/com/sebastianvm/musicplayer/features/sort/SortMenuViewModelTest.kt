package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class SortMenuViewModelTest :
    FreeSpec({
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository

        beforeTest { sortPreferencesRepositoryDep = FakeSortPreferencesRepository() }

        fun TestScope.getSubject(
            listType: SortableListType = SortableListType.AllTracks
        ): SortMenuViewModel {
            return SortMenuViewModel(
                arguments = SortMenuArguments(listType),
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                viewModelScope = this,
            )
        }

        "init sets state and subscribes to changes in preferences" -
            {
                "for all tracks" {
                    sortPreferencesRepositoryDep.allTracksSortPreferences.value =
                        MediaSortPreferences(
                            sortOption = SortOptions.Track,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                    val subject = getSubject(listType = SortableListType.AllTracks)
                    testViewModelState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forTracks,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forTracks,
                                selectedSort = SortOptions.Track,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.allTracksSortPreferences.value =
                            MediaSortPreferences(
                                sortOption = SortOptions.Artist,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )

                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forTracks,
                                selectedSort = SortOptions.Artist,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }

                "for genre" {
                    sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
                        mapOf(
                            GENRE_ID to
                                MediaSortPreferences(
                                    sortOption = SortOptions.Track,
                                    sortOrder = MediaSortOrder.ASCENDING,
                                )
                        )

                    val subject = getSubject(listType = SortableListType.Genre(GENRE_ID))
                    testViewModelState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forTracks,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forTracks,
                                selectedSort = SortOptions.Track,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
                            mapOf(
                                GENRE_ID to
                                    MediaSortPreferences(
                                        sortOption = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            )

                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forTracks,
                                selectedSort = SortOptions.Artist,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }

                "for playlist" {
                    sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                        mapOf(
                            PLAYLIST_ID to
                                MediaSortPreferences(
                                    sortOption = SortOptions.Track,
                                    sortOrder = MediaSortOrder.ASCENDING,
                                )
                        )

                    val subject = getSubject(listType = SortableListType.Playlist(PLAYLIST_ID))
                    testViewModelState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forPlaylist,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forPlaylist,
                                selectedSort = SortOptions.Track,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                            mapOf(
                                PLAYLIST_ID to
                                    MediaSortPreferences(
                                        sortOption = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            )

                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forPlaylist,
                                selectedSort = SortOptions.Artist,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }

                "for albums" {
                    sortPreferencesRepositoryDep.albumListSortPreferences.value =
                        MediaSortPreferences(
                            sortOption = SortOptions.Album,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )

                    val subject = getSubject(listType = SortableListType.Albums)
                    testViewModelState(subject) {
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forAlbums,
                                selectedSort = null,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forAlbums,
                                selectedSort = SortOptions.Album,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.albumListSortPreferences.value =
                            MediaSortPreferences(
                                sortOption = SortOptions.Artist,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                        awaitItem() shouldBe
                            SortMenuState(
                                sortOptions = SortOptions.forAlbums,
                                selectedSort = SortOptions.Artist,
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
                            testViewModelState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Track,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.forTracks,
                                        selectedSort = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Artist,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.forTracks,
                                        selectedSort = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            }
                        }

                        "for genre" {
                            val subject = getSubject(listType = SortableListType.Genre(GENRE_ID))
                            testViewModelState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Track,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.forTracks,
                                        selectedSort = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Artist,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.forTracks,
                                        selectedSort = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            }
                        }

                        "for playlist" {
                            val subject =
                                getSubject(listType = SortableListType.Playlist(PLAYLIST_ID))
                            testViewModelState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Track,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.forPlaylist,
                                        selectedSort = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Artist,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.forPlaylist,
                                        selectedSort = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.DESCENDING,
                                    )
                            }
                        }

                        "for albums" {
                            val subject = getSubject(listType = SortableListType.Albums)
                            testViewModelState(subject) {
                                skipItems(2)
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Album,
                                        currentSortOrder = MediaSortOrder.ASCENDING,
                                    )
                                )
                                awaitItem() shouldBe
                                    SortMenuState(
                                        sortOptions = SortOptions.forAlbums,
                                        selectedSort = SortOptions.Artist,
                                        sortOrder = MediaSortOrder.ASCENDING,
                                    )
                                subject.handle(
                                    SortMenuUserAction.MediaSortOptionClicked(
                                        newSortOption = SortOptions.Artist,
                                        selectedSort = SortOptions.Artist,
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
