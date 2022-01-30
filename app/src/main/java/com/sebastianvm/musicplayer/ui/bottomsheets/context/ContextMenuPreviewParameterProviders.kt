package com.sebastianvm.musicplayer.ui.bottomsheets.context

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.util.SortOrder

class ContextMenuStatePreviewParameterProvider : PreviewParameterProvider<BaseContextMenuState> {
    override val values = sequenceOf(
        AlbumContextMenuState(
            mediaId = "1",
            menuTitle = "La Promesa",
            listItems = listOf(
                ContextMenuItem.Play,
                ContextMenuItem.ViewArtists,
                ContextMenuItem.ViewAlbum
            ),
            selectedSort = MediaMetadataCompat.METADATA_KEY_ALBUM,
            sortOrder = SortOrder.DESCENDING
        )
    )
}