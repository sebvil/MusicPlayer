package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavController

object NavRoutes {
    const val LIBRARY = "LIBRARY"
    const val LIBRARY_ROOT = "LIBRARY_ROOT"
    const val TRACKS_ROOT = "TRACKS_ROOT"
    const val ARTISTS_ROOT = "ARTISTS_ROOT"
    const val ALBUMS_ROOT = "ALBUMS_ROOT"
    const val GENRES_ROOT = "GENRES_ROOT"
    const val PLAYLISTS_ROOT = "PLAYLISTS_ROOT"
    const val ALBUM = "ALBUM"
    const val ARTIST = "ARTIST"
    const val PLAYER = "PLAYER"
    const val SEARCH = "SEARCH"
    const val SORT = "SORT"
    const val CONTEXT = "CONTEXT"
    const val MEDIA_ARTISTS = "MEDIA_ARTISTS"
    const val QUEUE = "QUEUE"
}

object NavArgs {
    const val TRACK_LIST_NAME = "trackListName"
    const val ALBUM_ID = "albumId"
    const val ARTIST_ID = "artistName"
    const val SCREEN = "screen"
    const val SORT_OPTION = "sortOption"
    const val SORT_ORDER = "sortOrder"
    const val MEDIA_ID = "mediaId"
    const val MEDIA_TYPE = "mediaType"
    const val MEDIA_GROUP_ID = "mediaGroupId"
    const val MEDIA_GROUP_TYPE = "mediaGroupType"
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
