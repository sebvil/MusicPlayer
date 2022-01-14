package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.ArtistRowState

class ArtistsBottomSheetStatePreviewParameterProvider :
    PreviewParameterProvider<ArtistsBottomSheetState> {
    override val values = sequenceOf(
        ArtistsBottomSheetState(
            artistsList = listOf(
                ArtistRowState(artistName = "Melendi", shouldShowContextMenu = false),
                ArtistRowState(artistName = "Carlos Vives", shouldShowContextMenu = false)
            ),
            mediaGroup = MediaGroup(MediaType.ALL_TRACKS, "123")
        )
    )
}
