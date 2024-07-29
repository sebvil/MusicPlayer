package com.sebastianvm.musicplayer.features.artistsmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.ListItem
import com.sebastianvm.musicplayer.core.designsystems.components.Text
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.core.ui.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments

data class ArtistsMenuUiComponent(
    val arguments: ArtistsMenuArguments,
    val navController: NavController,
) :
    BaseUiComponent<
        UiState<ArtistsMenuState>,
        ArtistsMenuUserAction,
        ArtistsMenuStateHolder,
    >() {

    override fun createStateHolder(services: Services): ArtistsMenuStateHolder {
        return ArtistsMenuStateHolder(
            arguments = arguments,
            artistRepository = services.repositoryProvider.artistRepository,
            navController = navController,
            features = services.featureRegistry,
        )
    }

    @Composable
    override fun Content(
        state: UiState<ArtistsMenuState>,
        handle: Handler<ArtistsMenuUserAction>,
        modifier: Modifier,
    ) {
        ArtistsMenu(uiState = state, handle = handle, modifier = modifier)
    }
}

@Composable
fun ArtistsMenu(
    uiState: UiState<ArtistsMenuState>,
    handle: Handler<ArtistsMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    UiStateScreen(uiState = uiState, modifier = modifier, emptyScreen = {}) { state ->
        ArtistsMenu(state = state, handle = handle, modifier = Modifier)
    }
}

@Composable
fun ArtistsMenu(
    state: ArtistsMenuState,
    handle: Handler<ArtistsMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().navigationBarsPadding()) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = RString.artists),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(state.artists, key = { item -> item.id }) { item ->
                ArtistRow(
                    state = item,
                    modifier =
                        Modifier.clickable { handle(ArtistsMenuUserAction.ArtistClicked(item.id)) },
                )
            }
        }
    }
}
