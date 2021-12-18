package com.sebastianvm.musicplayer.ui.bottomsheets.context

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.util.SortOrder

class ContextMenuStatePreviewParameterProvider : PreviewParameterProvider<ContextMenuState> {
    override val values = sequenceOf(
        ContextMenuState(
            listItems = listOf(
                ContextMenuItem.Play,
                ContextMenuItem.ViewArtists,
                ContextMenuItem.ViewAlbum
            ),
            screen = BrowseTree.TRACKS_ROOT,
            mediaId = "1",
            selectedSort = MediaMetadataCompat.METADATA_KEY_ALBUM,
            sortOrder = SortOrder.DESCENDING
        )
    )
}