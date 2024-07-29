package com.sebastianvm.musicplayer.features.artist.screen

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
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController

data class ArtistUiComponent(
    val arguments: ArtistArguments,
    val navController: NavController,
) : BaseUiComponent<ArtistState, ArtistUserAction, ArtistStateHolder>() {

    override fun createStateHolder(services: Services): ArtistStateHolder {
        return ArtistStateHolder(
            arguments = arguments,
            artistRepository = services.repositoryProvider.artistRepository,
            navController = navController,
        )
    }

    @Composable
    override fun Content(
        state: ArtistState,
        handle: Handler<ArtistUserAction>,
        modifier: Modifier,
    ) {
        ArtistScreen(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun ArtistScreen(
    state: ArtistState,
    handle: Handler<ArtistUserAction>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        when (state) {
            is ArtistState.Data -> {
                ArtistScreen(state = state, handle = handle)
            }
            is ArtistState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    state: ArtistState.Data,
    handle: Handler<ArtistUserAction>,
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
                    IconButton(onClick = { handle(ArtistUserAction.BackClicked) }) {
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
    state: ArtistState.Data,
    handle: Handler<ArtistUserAction>,
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
    state: ArtistScreenSection,
    handle: Handler<ArtistUserAction>,
) {
    item(key = state.title) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = state.title),
                    style = MaterialTheme.typography.headlineMedium,
                )
            })
    }

    items(items = state.albums, key = { it.id }) { album ->
        AlbumRow(
            state = album,
            modifier = Modifier.clickable { handle(ArtistUserAction.AlbumClicked(album)) },
            trailingContent = {
                OverflowIconButton(
                    onClick = { handle(ArtistUserAction.AlbumMoreIconClicked(albumId = album.id)) })
            },
        )
    }
}
