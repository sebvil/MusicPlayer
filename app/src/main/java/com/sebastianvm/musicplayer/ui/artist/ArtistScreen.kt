package com.sebastianvm.musicplayer.ui.artist

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface

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
                    IconButton(onClick = { screenViewModel.handle(ArtistUserAction.UpButtonClicked) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )

        }
    ) {
        ArtistLayout(viewModel = screenViewModel)
    }
}


@ScreenPreview
@Composable
fun ArtistScreenPreview(@PreviewParameter(ArtistStatePreviewParameterProvider::class) state: ArtistState) {
    ScreenPreview {
        ArtistLayout(viewModel = DefaultViewModelInterfaceProvider.getDefaultInstance(state))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistLayout(viewModel: ViewModelInterface<ArtistState, ArtistUserAction>) {
    val state by viewModel.state.collectAsState()
    with(state) {
        LazyColumn {
            items(
                items = (albumsForArtistItems ?: listOf()) + (appearsOnForArtistItems ?: listOf())
            ) { item ->
                when (item) {
                    is ArtistScreenItem.SectionHeaderItem -> {
                        ListItem(headlineText = {
                            Text(
                                text = stringResource(id = item.sectionName),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        })

                    }
                    is ArtistScreenItem.AlbumRowItem -> {
                        ModelListItem(
                            state = item.state,
                            modifier = Modifier.clickable {
                                viewModel.handle(ArtistUserAction.AlbumClicked(item.state.id))
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = {
                                        viewModel.handle(
                                            ArtistUserAction.AlbumOverflowMenuIconClicked(
                                                item.state.id
                                            )
                                        )
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(id = com.sebastianvm.commons.R.drawable.ic_overflow),
                                        contentDescription = stringResource(id = com.sebastianvm.commons.R.string.more)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

