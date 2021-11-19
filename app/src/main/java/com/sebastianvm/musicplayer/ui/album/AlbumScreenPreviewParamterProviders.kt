package com.sebastianvm.musicplayer.ui.album

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState

class AlbumStatePreviewParameterProvider :
    PreviewParameterProvider<AlbumState> {
    override val values = sequenceOf(
        AlbumState(
            albumGid = "0",
            albumHeaderItem =  HeaderWithImageState(
                image = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.ResourceValue(value = R.string.album_art_for_album,
                        arrayOf("Sobre el amor y sus efectos secundarios")),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art)

                ),
                title = DisplayableString.StringValue("10:20:40")
            ), listOf(
                TrackRowState("0", "La Promesa", "Melendi"),
                TrackRowState("1", "La Promesa", "Melendi"),
                TrackRowState("2", "La Promesa", "Melendi"),
                TrackRowState("3", "La Promesa", "Melendi")
            )
        )
    )
}