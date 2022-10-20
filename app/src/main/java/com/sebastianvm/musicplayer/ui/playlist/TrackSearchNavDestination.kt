package com.sebastianvm.musicplayer.ui.playlist

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.NewScreen
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class TrackSearchArguments(val playlistId: Long) : NavigationArguments

fun NavGraphBuilder.trackSearchNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<TrackSearchViewModel>(
        destination = NavigationRoute.TrackSearch,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        val context = LocalContext.current
        NewScreen(
            screenViewModel = viewModel,
            eventHandler = { event ->
                when (event) {
                    is TrackSearchUiEvent.ShowConfirmationToast -> {
                        Toast.makeText(
                            context,
                            ResUtil.getString(
                                context,
                                R.string.track_added_to_playlist,
                                event.trackName
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            TrackSearchLayout(
                state = state,
                screenDelegate = delegate
            )
        }
    }
}

