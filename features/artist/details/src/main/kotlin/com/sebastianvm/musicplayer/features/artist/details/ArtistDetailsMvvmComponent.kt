package com.sebastianvm.musicplayer.features.artist.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.ListItem
import com.sebastianvm.musicplayer.core.designsystems.components.OverflowIconButton
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.kspannotations.MvvmComponent

@MvvmComponent(vmClass = ArtistDetailsViewModel::class)
@Composable
fun ArtistDetails(
    state: ArtistDetailsState,
    handle: Handler<ArtistDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (state) {
            is ArtistDetailsState.Data -> {
                ArtistDetails(state = state, handle = handle)
            }
            is ArtistDetailsState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetails(
    state: ArtistDetailsState.Data,
    handle: Handler<ArtistDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = state.artistName) },
                navigationIcon = {
                    IconButton(onClick = { handle(ArtistDetailsUserAction.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = RString.back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { paddingValues ->
        ArtistLayout(state = state, handle = handle, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
fun ArtistLayout(
    state: ArtistDetailsState.Data,
    handle: Handler<ArtistDetailsUserAction>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
        state.artistAlbumsSection?.let { section ->
            artistScreenSection(state = section, handle = handle)
        }

        state.artistAppearsOnSection?.let { section ->
            artistScreenSection(state = section, handle = handle)
        }
    }
}

fun LazyListScope.artistScreenSection(
    state: ArtistDetailsSection,
    handle: Handler<ArtistDetailsUserAction>,
) {
    item(key = state.title) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = state.title),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        )
    }

    items(items = state.albums, key = { it.id }) { album ->
        AlbumRow(
            state = album,
            modifier = Modifier.clickable { handle(ArtistDetailsUserAction.AlbumClicked(album)) },
            trailingContent = {
                OverflowIconButton(
                    onClick = {
                        handle(ArtistDetailsUserAction.AlbumMoreIconClicked(albumId = album.id))
                    }
                )
            },
        )
    }
}
