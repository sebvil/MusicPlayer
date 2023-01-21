package com.sebastianvm.musicplayer.ui.components.header

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun CollapsingImageHeader(
    mediaArtImageState: MediaArtImageState,
    listState: LazyListState,
    title: String,
    updateAlpha: (Float) -> Unit
) {
    val minSizeDp = 100.dp
    val minSizePx = with(LocalDensity.current) { minSizeDp.toPx() }
    val textHeight = remember {
        mutableStateOf(-1)
    }
    val density = LocalDensity.current
    val maxSizeDp = 500.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val originalImageHeaderSizeDp = min(screenWidth - 200.dp, maxSizeDp)
    val sizeDp = remember { mutableStateOf(originalImageHeaderSizeDp) }

    LaunchedEffect(key1 = listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.getOrNull(1) }
            .filterNotNull()
            .distinctUntilChanged { old, new ->
                (old.index == new.index && old.offset == new.offset)
            }
            .collect { listSecondItem ->
                // header is off-screen
                if (listSecondItem.index != 1) {
                    sizeDp.value = minSizeDp
                    updateAlpha(1f)
                    return@collect
                }
                // header is off-screen
                if (listSecondItem.offset < 0) {
                    sizeDp.value = minSizeDp
                    updateAlpha(1f)
                    return@collect
                }

                val offsetDiff = listSecondItem.offset
                if (offsetDiff < (minSizePx + textHeight.value)) {
                    sizeDp.value = minSizeDp
                    updateAlpha(0f)
                } else if (textHeight.value != -1) {
                    sizeDp.value = with(density) { offsetDiff.toDp() - textHeight.value.toDp() }
                    updateAlpha(0f)
                }
                if (offsetDiff < textHeight.value) {
                    updateAlpha(1f - (offsetDiff / textHeight.value.toFloat()))
                }
            }
    }

    val padding = (originalImageHeaderSizeDp - sizeDp.value).coerceIn(
        minimumValue = 0.dp,
        maximumValue = null
    )


    Column(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth()
            .padding(top = padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MediaArtImage(
            mediaArtImageState = mediaArtImageState,
            modifier = Modifier
                .size(sizeDp.value)
        )
        Text(
            text = title,
            modifier = Modifier
                .onSizeChanged {
                    textHeight.value = it.height
                },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center
        )
    }

}