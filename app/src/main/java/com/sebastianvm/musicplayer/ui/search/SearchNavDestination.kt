package com.sebastianvm.musicplayer.ui.search

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.searchNavDestination() {
    composable(NavRoutes.SEARCH) {
        val screenViewModel = hiltViewModel<SearchViewModel>()
        SearchScreen(screenViewModel)
    }
}