package com.sebastianvm.musicplayer.ui.artist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class ArtistArguments(val artistId: Long) : NavigationArguments

fun NavGraphBuilder.artistNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<ArtistViewModel>(
        destination = NavigationRoute.Artist,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        Screen(
            screenViewModel = viewModel,
            eventHandler = {},
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            ArtistScreen(
                state = state,
                screenDelegate = delegate,
            )
        }
    }
}

