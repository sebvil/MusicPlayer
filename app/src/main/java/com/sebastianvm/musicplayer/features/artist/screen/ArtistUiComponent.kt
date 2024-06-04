package com.sebastianvm.musicplayer.features.artist.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.DeprecatedModelListItem
import com.sebastianvm.musicplayer.ui.util.compose.ScreenScaffold
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState

data class ArtistUiComponent(
    override val arguments: ArtistArguments,
    val navController: NavController,
) : BaseUiComponent<ArtistArguments, UiState<ArtistState>, ArtistUserAction, ArtistStateHolder>() {

    override fun createStateHolder(dependencies: DependencyContainer): ArtistStateHolder {
        return getArtistStateHolder(dependencies, arguments, navController)
    }

    @Composable
    override fun Content(
        state: UiState<ArtistState>,
        handle: Handler<ArtistUserAction>,
        modifier: Modifier
    ) {
        ArtistScreen(
            uiState = state,
            handle = handle,
            modifier = modifier
        )
    }
}

@Composable
fun ArtistScreen(
    uiState: UiState<ArtistState>,
    handle: Handler<ArtistUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(uiState = uiState, emptyScreen = {}, modifier) { state ->
        ArtistScreen(
            state = state,
            handle = handle,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistScreen(
    state: ArtistState,
    handle: Handler<ArtistUserAction>,
    modifier: Modifier = Modifier
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    ScreenScaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = state.artistName) },
                navigationIcon = {
                    IconButton(onClick = {
                        handle(ArtistUserAction.BackClicked)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        ArtistLayout(
            state = state,
            handle = handle,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun ArtistLayout(
    state: ArtistState,
    handle: Handler<ArtistUserAction>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
        items(items = state.listItems) { item ->
            when (item) {
                is ArtistScreenItem.SectionHeaderItem -> {
                    ListItem(headlineContent = {
                        Text(
                            text = stringResource(id = item.sectionType.sectionName),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    })
                }

                is ArtistScreenItem.AlbumRowItem -> {
                    DeprecatedModelListItem(
                        state = item.state,
                        modifier = Modifier.clickable {
                            handle(ArtistUserAction.AlbumClicked(item.id))
                        },
                        onMoreClicked = {
                            handle(ArtistUserAction.AlbumMoreIconClicked(item.id))
                        }
                    )
                }
            }
        }
    }
}
