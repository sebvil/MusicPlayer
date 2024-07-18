package com.sebastianvm.musicplayer.features.playlist.list

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.core.designsystems.previews.PreviewComponents
import com.sebastianvm.musicplayer.core.designsystems.previews.ThemedPreview

// class PlaylistListStatePreviewParamProvider : PreviewParameterProvider<PlaylistListState> {
//    private val playlistsList: List<ModelListItem.State>
//        get() = (1..10).map {
//            Playlist(
//                id = it.toLong(),
//                playlistName = PreviewUtil.randomString(),
//            ).toModelListItemState()
//        }
//    override val values: Sequence<PlaylistListState>
//        get() = sequenceOf(
//            PlaylistListState(
//                playlistList = playlistsList,
//                isCreatePlaylistDialogOpen = false,
//                isPlaylistCreationErrorDialogOpen = false
//            ),
//            PlaylistListState(
//                playlistList = playlistsList,
//                isCreatePlaylistDialogOpen = true,
//                isPlaylistCreationErrorDialogOpen = false
//            ),
//            PlaylistListState(
//                playlistList = playlistsList,
//                isCreatePlaylistDialogOpen = false,
//                isPlaylistCreationErrorDialogOpen = true
//            ),
//            PlaylistListState(
//                playlistList = playlistsList,
//                isCreatePlaylistDialogOpen = true,
//                isPlaylistCreationErrorDialogOpen = true
//            )
//        )
// }

// // Note: You can launch the preview on device to see the dialogs
// @ScreenPreview
// @Composable
// private fun
// PlaylistListScreenPreview(@PreviewParameter(PlaylistListStatePreviewParamProvider::class) state:
// PlaylistListState) {
//    ScreenPreview {
//        PlaylistListScreen(
//            state = state,
//            onSortByClicked = {},
//            onDismissPlaylistCreationErrorDialog = {},
//            onCreatePlaylistCLicked = {},
//            navigateToPlaylist = {},
//            openPlaylistContextMenu = {},
//            navigateBack = {}
//        )
//    }
// }

@PreviewComponents
@Composable
private fun PlaylistCreationErrorDialogPreview() {
    ThemedPreview { PlaylistCreationErrorDialog {} }
}

@PreviewComponents
@Composable
private fun CreatePlaylistDialogPreview() {
    ThemedPreview { CreatePlaylistDialog(onDismiss = {}, onConfirm = {}) }
}
