package com.sebastianvm.musicplayer.features.track.list

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.util.resources.RString
import kotlin.math.max
import kotlin.math.min

object Header {
    sealed interface State {
        data object None : State

        data class Simple(val title: String) : State

        data class WithImage(val title: String, val imageState: MediaArtImageState) : State
    }
}

// TODO refactor to use AnimatedContent APIs
@Composable
fun HeaderWithImageModelList(
    state: Header.State.WithImage,
    listState: LazyListState,
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val density = LocalDensity.current
    val minSizeDp = 100.dp
    val minSizePx = with(density) { minSizeDp.toPx() }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxSizeDp = min(screenWidth - 200.dp, 500.dp)
    val maxSizePx = with(density) { maxSizeDp.toPx() }
    var sizeDp by remember { mutableStateOf(maxSizeDp) }
    val sizePx by remember(sizeDp) { derivedStateOf { with(density) { sizeDp.toPx() } } }
    var offset by remember { mutableStateOf(0.dp) }
    val offsetPx by remember(offset) { derivedStateOf { with(density) { offset.toPx() } } }

    var fullHeaderHeight by remember { mutableIntStateOf(0) }

    val fullHeaderHeaderHeightDp by remember {
        derivedStateOf { with(density) { fullHeaderHeight.toDp() } }
    }

    var topBarHeight by remember { mutableIntStateOf(-1) }

    var topBarAlpha by remember { mutableFloatStateOf(0f) }

    var imageAlpha by remember { mutableFloatStateOf(1f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val sizeChange: Float
                val offsetChange: Float
                if (available.y < 0) {
                    // Header is off-screen, no need to handle
                    if (offsetPx <= -fullHeaderHeight.toFloat()) return Offset.Zero
                    sizeChange = max(minSizePx - sizePx, available.y)
                    offsetChange = max(available.y - sizeChange, -fullHeaderHeight - offsetPx)
                } else {
                    if (
                        listState.firstVisibleItemIndex != 0 ||
                            listState.firstVisibleItemScrollOffset != 0 ||
                            sizeDp == maxSizeDp
                    ) {
                        return Offset.Zero
                    }

                    offsetChange = min(-offsetPx, available.y)
                    sizeChange = min(maxSizePx - sizePx, available.y - offsetChange)
                }
                sizeDp = with(density) { (sizePx + sizeChange).toDp() }
                offset = with(density) { (offsetPx + offsetChange).toDp() }
                topBarAlpha =
                    if (offset == 0.dp) {
                        0f
                    } else {
                        ((1f / (fullHeaderHeight - sizePx)) * (topBarHeight - offsetPx) -
                                (sizePx) / (fullHeaderHeight - sizePx))
                            .coerceIn(minimumValue = 0f, maximumValue = 1f)
                    }

                imageAlpha =
                    if (offset == 0.dp) {
                        1f
                    } else {
                        (1f - ((topBarHeight - offsetPx) / sizePx))
                            .coerceIn(minimumValue = 0f, maximumValue = 1f)
                            .also { Log.i("Offset", "$it") }
                    }

                return available.copy(y = sizeChange + offsetChange, x = 0f)
            }
        }
    }

    val animatedSize by animateDpAsState(targetValue = sizeDp, label = "size")

    Box(modifier = modifier.nestedScroll(nestedScrollConnection)) {
        TopBar(
            title = state.title,
            alpha = topBarAlpha,
            onSizeChanged = { topBarHeight = it },
            onBackButtonClicked = onBackButtonClicked,
        )

        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .offset { IntOffset(x = 0, y = offset.roundToPx()) }
                    .onSizeChanged { size -> fullHeaderHeight = size.height },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MediaArtImage(
                mediaArtImageState = state.imageState,
                modifier = Modifier.size(animatedSize).alpha(imageAlpha),
            )
            Text(
                text = state.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(1 - topBarAlpha),
            )
        }

        content(
            PaddingValues(
                top = (fullHeaderHeaderHeightDp + offset).coerceAtLeast(0.dp),
                bottom = LocalPaddingValues.current.calculateBottomPadding(),
            )
        )
    }
}

@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    onSizeChanged: (Int) -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .zIndex(1f)
                .background(MaterialTheme.colorScheme.background.copy(alpha = alpha))
                .onSizeChanged { onSizeChanged(it.height) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackButtonClicked) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(id = RString.back),
            )
        }
        Text(
            text = title,
            modifier = Modifier.alpha(alpha),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
