package com.sebastianvm.musicplayer.ui.album

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState

class AlbumStatePreviewParameterProvider :
    PreviewParameterProvider<AlbumState> {
    override val values = sequenceOf(
        AlbumState(
            albumId = "0",
            albumHeaderItem =  HeaderWithImageState(
                image = Uri.EMPTY,
                title = DisplayableString.StringValue("10:20:40")
            ), listOf(
                TrackRowState("0", "La Promesa", "Melendi","Un alumno mas"),
                TrackRowState("1", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("2", "La Promesa", "Melendi", "Un alumno mas"),
                TrackRowState("3", "La Promesa", "Melendi", "Un alumno mas")
            )
        )
    )
}
