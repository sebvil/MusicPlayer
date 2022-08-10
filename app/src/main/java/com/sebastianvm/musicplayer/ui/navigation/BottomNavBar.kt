package com.sebastianvm.musicplayer.ui.navigation

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.NavEvent


sealed class Screen(
    val destination: NavigationDestination,
    @StringRes val resourceId: Int,
    @DrawableRes val icon: Int
) {
    object Library : Screen(NavigationDestination.LibraryRoot, R.string.library, R.drawable.ic_song)
    object Queue : Screen(NavigationDestination.Queue, R.string.queue, R.drawable.ic_queue)
    object Player : Screen(NavigationDestination.MusicPlayer, R.string.player, R.drawable.ic_play)
    object Search : Screen(NavigationDestination.Search, R.string.search, R.drawable.ic_search)
}

val items = listOf(
    Screen.Library,
    Screen.Queue,
    Screen.Player,
    Screen.Search
)

@Composable
fun BottomNavBar(navigationDelegate: NavigationDelegate) {
    CompositionLocalProvider(LocalRippleTheme provides LocalRippleTheme.current) {
        NavigationBar {
            items.forEach { screen ->
                val isSelected by navigationDelegate.isRouteInGraphAsState(screen.destination.navigationRoute)
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            contentDescription = null,
                        )
                    },
                    label = { Text(text = stringResource(screen.resourceId)) },
                    selected = isSelected,
                    onClick = { navigationDelegate.handleNavEvent(NavEvent.NavigateToScreen(screen.destination)) }
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BottomNavBarPreview() {
    ThemedPreview {
        val navController = rememberNavController()
        BottomNavBar(NavigationDelegate(navController))
    }
}
