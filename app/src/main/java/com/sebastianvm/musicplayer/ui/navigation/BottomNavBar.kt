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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val icon: Int) {
    object Library : Screen(NavRoutes.LIBRARY, R.string.library, R.drawable.ic_song)
    object Queue : Screen(NavRoutes.QUEUE, R.string.queue, R.drawable.ic_queue)
    object Player : Screen(NavRoutes.PLAYER, R.string.player, R.drawable.ic_play)
    object Search : Screen(NavRoutes.SEARCH, R.string.search, R.drawable.ic_search)
}

val items = listOf(
    Screen.Library,
    Screen.Queue,
    Screen.Player,
    Screen.Search
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    CompositionLocalProvider(LocalRippleTheme provides LocalRippleTheme.current) {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.icon),
                            contentDescription = null,
                        )
                    },
                    label = { Text(text = stringResource(screen.resourceId)) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                            navController.popBackStack(
                                navController.graph.findStartDestination().id,
                                inclusive = false
                            )

                        } else {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    }
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
        BottomNavBar(navController)
    }
}
