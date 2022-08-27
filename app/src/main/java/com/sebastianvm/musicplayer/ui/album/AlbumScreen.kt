package com.sebastianvm.musicplayer.ui.album

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.TrackList
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface

@Composable
fun AlbumScreen(
    screenViewModel: AlbumViewModel,
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate,
        topBar = { state ->
            LibraryTopBar(title = state.albumName, delegate = object : LibraryTopBarDelegate {})
        }
    ) {
        AlbumLayout(viewModel = screenViewModel, navigationDelegate = navigationDelegate)
    }
}

@ScreenPreview
@Composable
fun AlbumScreenPreview(
    @PreviewParameter(AlbumStatePreviewParameterProvider::class) state: AlbumState
) {
    ScreenPreview {
        AlbumLayout(
            viewModel = DefaultViewModelInterfaceProvider.getDefaultInstance(state),
            navigationDelegate = NavigationDelegate(rememberNavController())
        )
    }
}


@Composable
fun AlbumLayout(
    viewModel: ViewModelInterface<AlbumState, AlbumUserAction>,
    navigationDelegate: NavigationDelegate
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val maxWidth = 500.dp
    val density = LocalDensity.current
    val minWidth = with(density) { 50.dp.toPx() }
    val initialWidth = min(LocalConfiguration.current.screenWidthDp.dp - 100.dp, maxWidth)
    val initialWidthPx = with(density) { initialWidth.toPx() }
    var size by remember {
        mutableStateOf(initialWidthPx)
    }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // try to consume before LazyColumn to collapse toolbar if needed, hence pre-scroll
                val delta = available.y
                Log.i("Scroll", "$delta")
                size = kotlin.math.max(minWidth, size + delta)
                // here's the catch: let's pretend we consumed 0 in any case, since we want
                // LazyColumn to scroll anyway for good UX
                // We're basically watching scroll without taking it
                return Offset.Zero
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            // attach as a parent to the nested scroll system
            .nestedScroll(nestedScrollConnection)
    ) {
        MediaArtImage(
            uri = state.imageUri,
            contentDescription = stringResource(
                id = R.string.album_art_for_album,
                state.albumName
            ),
            backupContentDescription = R.string.placeholder_album_art,
            backupResource = R.drawable.ic_album,
            modifier = Modifier
                .size(with(density) { size.toDp() }),
            contentScale = ContentScale.Fit
        )

        TrackList(
            viewModel = hiltViewModel(),
            navigationDelegate = navigationDelegate,
            contentPadding = PaddingValues(top = initialWidth),
            listState
        ) {}
    }
}
