package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

data class TrackProgressState(
    val trackLengthMs: Long?,
    val currentPlaybackTimeMs: Long?
)

@Preview
@Composable
fun TrackProgress(
    @PreviewParameter(TrackProgressStatePreviewParameterProvider::class) trackProgressState: TrackProgressState,
    onProgressBarClicked: (position: Int) -> Unit = {}
) {
    with(trackProgressState) {
        val progress =
            if (currentPlaybackTimeMs == null || trackLengthMs == null || trackLengthMs == 0L) {
                0f
            } else {
                currentPlaybackTimeMs.toFloat() / trackLengthMs.toFloat()
            }

        val currentDuration = MinutesSecondsTime.fromMs(currentPlaybackTimeMs ?: 0)
        val trackDuration = MinutesSecondsTime.fromMs(trackLengthMs ?: 0)
        Column(modifier = Modifier.padding(all = AppDimensions.spacing.medium)) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppDimensions.spacing.xSmall)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val xOffset = offset.x
                            val percentPosition = (xOffset / size.width) * 100
                            onProgressBarClicked(percentPosition.toInt())
                        }
                    }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "%02d:%02d".format(currentDuration.minutes, currentDuration.seconds),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "%02d:%02d".format(trackDuration.minutes, trackDuration.seconds),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }


}