package com.sebastianvm.musicplayer.ui.library.tracks

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.DoubleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.util.SortOrder


interface TracksListScreenNavigationDelegate {
    fun navigateToPlayer()
    fun navigateUp()
    fun openSortMenu(sortOption: Int, sortOrder: SortOrder)
    fun openContextMenu()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksListScreen(
    screenViewModel: TracksListViewModel = viewModel(),
    bottomNavBar: @Composable () -> Unit,
    delegate: TracksListScreenNavigationDelegate
) {

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is TracksListUiEvent.NavigateToPlayer -> {
                    delegate.navigateToPlayer()
                }
                is TracksListUiEvent.ShowBottomSheet -> {
                    delegate.openSortMenu(event.sortOption, event.sortOrder)
                }
                is TracksListUiEvent.OpenContextMenu -> {
                    delegate.openContextMenu()
                }
            }
        },
        topBar = { state ->
            TopBar(state = state, delegate = object : TopBarDelegate {
                override fun navigateUp() {
                    delegate.navigateUp()
                }

                override fun sortByClicked() {
                    screenViewModel.handle(TracksListUserAction.SortByClicked)
                }
            })
        },
        bottomNavBar = bottomNavBar,
    ) { state ->
        TracksListLayout(
            state = state,
            delegate = object : TracksListScreenDelegate {
                override fun onTrackClicked(trackGid: String) {
                    screenViewModel.handle(
                        TracksListUserAction.TrackClicked(
                            trackGid
                        )
                    )
                }

                override fun onTrackLongPressed(trackGid: String) {
                    screenViewModel.handle(
                        TracksListUserAction.TrackLongPressed(
                            trackGid
                        )
                    )
                }
            })
    }
}


interface TopBarDelegate {
    fun navigateUp() = Unit
    fun sortByClicked() = Unit
}

@Composable
fun TopBar(state: TracksListState, delegate: TopBarDelegate) {
    SmallTopAppBar(
        title = {
            Text(text = state.tracksListTitle.getString())
        },
        navigationIcon = {
            IconButton(onClick = { delegate.navigateUp() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            IconButton(onClick = { delegate.sortByClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = stringResource(id = R.string.sort_by)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreview(@PreviewParameter(TracksListStatePreviewParameterProvider::class) state: TracksListState) {
    ThemedPreview {
        TopBar(state = state, delegate = object : TopBarDelegate {})
    }
}


interface TracksListScreenDelegate {
    fun onTrackClicked(trackGid: String)
    fun onTrackLongPressed(trackGid: String) = Unit
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TracksListScreenPreview(@PreviewParameter(TracksListStatePreviewParameterProvider::class) state: TracksListState) {
    ScreenPreview(topBar = { TopBar(state = state, delegate = object : TopBarDelegate {}) }) {
        TracksListLayout(state = state, delegate = object : TracksListScreenDelegate {
            override fun onTrackClicked(trackGid: String) = Unit
        })
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TracksListLayout(
    state: TracksListState,
    delegate: TracksListScreenDelegate
) {
    LazyColumn {
        items(state.tracksList) { item ->
            DoubleLineListItem(
                afterListContent = { onClick ->
                    IconButton(onClick = onClick, modifier = Modifier.padding(horizontal = AppDimensions.spacing.xSmall)) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = DisplayableString.StringValue("More") // TODO extract resource
                                .getString(),
                        )
                    }

                },
                delegate = object : ListItemDelegate {
                    override fun onItemClicked() {
                        delegate.onTrackClicked(item.trackGid)
                    }

                    override fun onSecondaryActionIconClicked() {
                        delegate.onTrackLongPressed(item.trackGid)
                    }
                },
                secondaryText = {
                    Text(
                        text = item.artists,
                        modifier = Modifier.alpha(0.6f),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }) {
                Text(
                    text = item.trackName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}


