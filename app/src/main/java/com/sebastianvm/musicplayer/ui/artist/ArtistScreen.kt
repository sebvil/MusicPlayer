package com.sebastianvm.musicplayer.ui.artist

import android.content.res.Configuration
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.AlbumRow
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ComposePreviews
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    screenViewModel: ArtistViewModel,
    navigationDelegate: NavigationDelegate,
) {
    val topBarState = rememberTopAppBarState()
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topBarState)
    }

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { state ->
            LargeTopAppBar(
                title = { Text(text = state.artistName) },
                navigationIcon = {
                    IconButton(onClick = { screenViewModel.onUpButtonClicked() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )

        }
    ) { state ->
        ArtistLayout(state = state, delegate = object : ArtistScreenDelegate {
            override fun albumRowClicked(albumId: Long) {
                screenViewModel.onAlbumClicked(albumId = albumId)
            }

            override fun onAlbumOverflowMenuIconClicked(albumId: Long) {
                screenViewModel.onAlbumOverflowMenuIconClicked(albumId = albumId)
            }
        })
    }
}

interface ArtistScreenDelegate : ArtistScreenRowDelegate

@ComposePreviews
@Composable
fun ArtistScreenPreview(@PreviewParameter(ArtistStatePreviewParameterProvider::class) state: ArtistState) {
    ScreenPreview {
        ArtistLayout(state = state, delegate = object : ArtistScreenDelegate {})
    }
}

@Composable
fun ArtistLayout(
    state: ArtistState,
    delegate: ArtistScreenDelegate
) {
    with(state) {
        LazyColumn {
            items(
                items = (albumsForArtistItems ?: listOf()) + (appearsOnForArtistItems ?: listOf())
            ) { item ->
                ArtistScreenRow(item = item, delegate = delegate)
            }
        }
    }
}

interface ArtistScreenRowDelegate {
    fun albumRowClicked(albumId: Long) = Unit
    fun onAlbumOverflowMenuIconClicked(albumId: Long) = Unit
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ArtistScreenRowPreview(@PreviewParameter(ArtistViewItemProvider::class) item: ArtistScreenItem) {
    ThemedPreview {
        ArtistScreenRow(item = item, delegate = object : ArtistScreenRowDelegate {})
    }
}


@Composable
fun ArtistScreenRow(
    @PreviewParameter(ArtistViewItemProvider::class) item: ArtistScreenItem,
    delegate: ArtistScreenRowDelegate
) {
    when (item) {
        is ArtistScreenItem.SectionHeaderItem -> {
            Text(
                text = stringResource(id = item.sectionName),
                modifier = Modifier.padding(
                    start = AppDimensions.spacing.mediumLarge,
                    end = AppDimensions.spacing.mediumLarge,
                    bottom = AppDimensions.spacing.small
                ),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        is ArtistScreenItem.AlbumRowItem -> {
            AlbumRow(
                state = item.state,
                modifier = Modifier.clickable { delegate.albumRowClicked(item.state.albumId) },
                onOverflowMenuIconClicked = { delegate.onAlbumOverflowMenuIconClicked(item.state.albumId) }
            )
        }
    }
}
