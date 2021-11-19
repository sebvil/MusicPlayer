package com.sebastianvm.musicplayer.ui.player

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt


class MusicPlayerStatePreviewParameterProvider : PreviewParameterProvider<MusicPlayerState> {
    override val values = sequenceOf(
        MusicPlayerState(
            isPlaying = false,
            trackName = "El Arrepentido",
            artists = "Melendi, Carlos Vives",
            trackLengthMs = null,
            currentPlaybackTimeMs = null,
            trackGid = null,
            albumGid = null,
            trackArt = MediaArt(
                uris = listOf(),
                contentDescription = DisplayableString.StringValue(""),
                backupResource = R.drawable.ic_album,
                backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art),
            )
        )
    )
}

class TrackProgressStatePreviewParameterProvider : PreviewParameterProvider<TrackProgressState> {
    override val values = sequenceOf(
        TrackProgressState(
            currentPlaybackTimeMs = 61600,
            trackLengthMs = 184000
        ),
        TrackProgressState(
            currentPlaybackTimeMs = null,
            trackLengthMs = null
        )
    )
}

class TrackInfoStatePreviewParameterProvider : PreviewParameterProvider<TrackInfoState> {
    override val values = sequenceOf(
        TrackInfoState(
            trackName = "El Arrepentido",
            artists = "Melendi, Carlos Vives"
        )
    )
}