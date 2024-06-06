package com.sebastianvm.musicplayer.features.artistsmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.components.UiStateScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelList
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState

data class ArtistsMenu(
    override val arguments: ArtistsMenuArguments,
    val navController: NavController
) : BaseUiComponent<ArtistsMenuArguments, UiState<ArtistsMenuState>, ArtistsMenuUserAction, ArtistsMenuStateHolder>() {

    override fun createStateHolder(dependencies: AppDependencies): ArtistsMenuStateHolder {
        return ArtistsMenuStateHolder(
            arguments = arguments,
            artistRepository = dependencies.repositoryProvider.artistRepository,
            navController = navController
        )
    }

    @Composable
    override fun Content(
        state: UiState<ArtistsMenuState>,
        handle: Handler<ArtistsMenuUserAction>,
        modifier: Modifier
    ) {
        ArtistsMenu(
            uiState = state,
            handle = handle,
            modifier = modifier
        )
    }
}

@Composable
fun ArtistsMenu(
    uiState: UiState<ArtistsMenuState>,
    handle: Handler<ArtistsMenuUserAction>,
    modifier: Modifier = Modifier
) {
    UiStateScreen(
        uiState = uiState,
        modifier = modifier,
        emptyScreen = {}
    ) { state ->
        ArtistsMenu(
            state = state,
            handle = handle,
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
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
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
