package com.sebastianvm.musicplayer.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sebastianvm.musicplayer.R


sealed class Screen(val route: String, @StringRes val resourceId: Int, @DrawableRes val icon: Int) {
    object Library : Screen(NavRoutes.LIBRARY, R.string.library, R.drawable.ic_song)
    object Player : Screen(NavRoutes.PLAYER, R.string.player, R.drawable.ic_play)
}

val items = listOf(
    Screen.Library,
    Screen.Player,
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = null,
                        modifier = Modifier.height(30.dp).padding(4.dp)
                    )
                },
                label = { Text(text = stringResource(screen.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
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
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomNavigation {
        items.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        painter = painterResource(id = screen.icon),
                        contentDescription = null,
                        modifier = Modifier.height(30.dp).padding(4.dp)
                    )
                },
                label = { Text(text = stringResource(screen.resourceId)) },
                selected = screen.route == NavRoutes.LIBRARY,
                onClick = {}
            )
        }
    }
}
