package com.sebastianvm.musicplayer.features.playlist.menu

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datatest.playlist.FakePlaylistRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuProps
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class PlaylistContextMenuViewModelTest :
    FreeSpec({
        lateinit var playlistRepositoryDep: FakePlaylistRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        var delegateDeletePlaylistInvocationCount = 0

        beforeTest {
            playlistRepositoryDep = FakePlaylistRepository()
            playbackManagerDep = FakePlaybackManager()
            delegateDeletePlaylistInvocationCount = 0
        }

        fun TestScope.getSubject(playlistId: Long = PLAYLIST_ID): PlaylistContextMenuViewModel {
            return PlaylistContextMenuViewModel(
                arguments = PlaylistContextMenuArguments(playlistId = playlistId),
                playlistRepository = playlistRepositoryDep,
                playbackManager = playbackManagerDep,
                vmScope = this,
                props =
                    MutableStateFlow(
                        PlaylistContextMenuProps(
                            deletePlaylist = { delegateDeletePlaylistInvocationCount++ }
                        )
                    ),
            )
        }

        "init sets state" {
            val playlist = FixtureProvider.playlist()
            playlistRepositoryDep.playlists.value = listOf(playlist)

            val subject = getSubject(playlistId = playlist.id)
            testViewModelState(subject) {
                awaitItem() shouldBe PlaylistContextMenuState.Loading
                awaitItem() shouldBe
                    PlaylistContextMenuState.Data(
                        playlistName = playlist.name,
                        playlistId = playlist.id,
                        showDeleteConfirmationDialog = false,
                    )
            }
        }

        "handle" -
            {
                "ConfirmPlaylistDeletionClicked deletes playlist" {
                    val subject = getSubject()
                    subject.handle(PlaylistContextMenuUserAction.ConfirmPlaylistDeletionClicked)
                    delegateDeletePlaylistInvocationCount shouldBe 1
                }

                "DeletePlaylistClicked and PlaylistDeletionCancelled toggle delete confirmation dialog" {
                    val playlist = FixtureProvider.playlist()
                    playlistRepositoryDep.playlists.value = listOf(playlist)

                    val subject = getSubject(playlistId = playlist.id)
                    testViewModelState(subject) {
                        awaitItemAs<PlaylistContextMenuState.Loading>()
                        awaitItemAs<PlaylistContextMenuState.Data>()
                            .showDeleteConfirmationDialog shouldBe false

                        subject.handle(PlaylistContextMenuUserAction.DeletePlaylistClicked)
                        awaitItemAs<PlaylistContextMenuState.Data>()
                            .showDeleteConfirmationDialog shouldBe true

                        subject.handle(PlaylistContextMenuUserAction.PlaylistDeletionCancelled)
                        awaitItemAs<PlaylistContextMenuState.Data>()
                            .showDeleteConfirmationDialog shouldBe false
                    }
                }

                "PlayPlaylistClicked plays playlist" {
                    val subject = getSubject()
                    subject.handle(PlaylistContextMenuUserAction.PlayPlaylistClicked)
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                MediaGroup.Playlist(PLAYLIST_ID),
                                initialTrackIndex = 0,
                            )
                        )
                }
            }
    }) {
    companion object {
        private const val PLAYLIST_ID = 0L
    }
}
