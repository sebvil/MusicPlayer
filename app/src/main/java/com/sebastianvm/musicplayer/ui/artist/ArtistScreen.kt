package com.sebastianvm.musicplayer.ui.artist

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    state: ArtistState,
    screenDelegate: ScreenDelegate<ArtistUserAction> = DefaultScreenDelegateProvider.getDefaultInstance()
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    ScreenLayout(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = state.artistName) },
                navigationIcon = {
                    IconButton(onClick = { screenDelegate.handle(ArtistUserAction.UpButtonClicked) }) {
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
        ArtistLayout(state, screenDelegate)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistLayout(state: ArtistState, screenDelegate: ScreenDelegate<ArtistUserAction>) {
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
                                screenDelegate.handle(ArtistUserAction.AlbumClicked(item.state.id))
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = {
                                        screenDelegate.handle(
                                            ArtistUserAction.AlbumOverflowMenuIconClicked(
                                                item.state.id
                                            )
                                        )
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_overflow),
                                        contentDescription = stringResource(id = R.string.more)
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

