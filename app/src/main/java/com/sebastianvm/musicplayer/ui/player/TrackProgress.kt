package com.sebastianvm.musicplayer.ui.player

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@JvmInline
value class Percentage(val percent: Float) {
    companion object {
        const val MAX = 100f
    }
}

val Int.percent: Percentage
    get() = Percentage(this.toFloat() / Percentage.MAX)

data class TrackProgressState(
    val currentPlaybackTime: MinutesSecondsTime,
    val trackLength: MinutesSecondsTime
) {
    val progress: Percentage
        get() = Percentage(
            currentPlaybackTime.toMilliseconds().toFloat() / trackLength.toMilliseconds().toFloat()
        )
}

@Composable
fun TrackProgress(
    state: TrackProgressState,
    onProgressBarClicked: (position: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = state.progress.percent,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val xOffset = offset.x
                        val percentPosition = (xOffset / size.width) * Percentage.MAX
                        onProgressBarClicked(percentPosition.toInt())
                    }
                }
        )
//        Row(
//            horizontalArrangement = Arrangement.SpaceBetween,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 4.dp)
//        ) {
//            Text(
//                text = state.currentPlaybackTime,
//                style = MaterialTheme.typography.bodyMedium,
//            )
//            Text(
//                text = state.trackLength,
//                style = MaterialTheme.typography.bodyMedium,
//            )
//        }
    }
}
