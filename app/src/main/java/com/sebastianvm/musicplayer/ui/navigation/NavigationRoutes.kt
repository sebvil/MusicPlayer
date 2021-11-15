package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavController
import com.sebastianvm.musicplayer.player.BrowseTree

object NavRoutes {
    const val LIBRARY = "LIBRARY"
    const val LIBRARY_ROOT = "LIBRARY_ROOT"
    const val GENRES_ROOT = BrowseTree.GENRES_ROOT
    const val TRACKS_ROOT = BrowseTree.TRACKS_ROOT
    const val ARTISTS_ROOT = BrowseTree.ARTISTS_ROOT
    const val ALBUMS_ROOT = BrowseTree.ALBUMS_ROOT
    const val ALBUM = "ALBUM"
    const val ARTIST = "ARTIST"
    const val PLAYER = "PLAYER"
}

object NavArgs {
    const val GENRE_NAME = "genreName"
    const val ALBUM_GID = "albumGid"
    const val ALBUM_NAME = "albumName"
    const val ARTIST_GID = "artistGid"
    const val ARTIST_NAME = "artistName"
}

fun createNavRoute(route: String, vararg parameters: String): String {
    return if (parameters.isEmpty()) {
        route
    } else {
        "$route?" + parameters.joinToString("&") { s -> "$s={$s}" }
    }
}

data class NavArgument<T>(val parameterName: String, val value: T)

fun NavController.navigateTo(route: String, vararg parameters: NavArgument<*>) {
    val navRoute = if (parameters.isEmpty()) {
        route
    } else {
        "$route?" + parameters.joinToString("&") { s -> "${s.parameterName}=${s.value}" }
    }
    this.navigate(navRoute)
}