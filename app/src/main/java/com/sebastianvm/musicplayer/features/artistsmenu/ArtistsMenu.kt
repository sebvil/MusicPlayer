package com.sebastianvm.musicplayer.features.artistsmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

@Composable
fun ArtistsMenu(stateHolder: ArtistsMenuStateHolder, modifier: Modifier = Modifier) {
    val uiState by stateHolder.currentState
    UiStateScreen(
        uiState = uiState,
        modifier = modifier.fillMaxSize(),
        emptyScreen = {}
    ) { state ->
        ArtistsMenu(
            state = state,
            handle = stateHolder::handle,
            modifier = Modifier
        )
    }
}

@Composable
fun ArtistsMenu(
    state: ArtistsMenuState,
    handle: Handler<ArtistsMenuUserAction>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.artists),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            modifier = Modifier.padding(top = 12.dp)
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        ModelList(
            state = state.modelListState,
            onBackButtonClicked = {},
            onItemClicked = { _, item ->
                handle(ArtistsMenuUserAction.ArtistClicked(item.id))
            }
        )
    }
}
