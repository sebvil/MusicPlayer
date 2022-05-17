package com.sebastianvm.musicplayer.ui.navigation

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val ARGS = "ARGS"

object NavRoutes {
    const val LIBRARY = "LIBRARY"
    const val LIBRARY_ROOT = "LIBRARY_ROOT"
    const val TRACKS_ROOT = "TRACKS_ROOT"
    const val ARTISTS_ROOT = "ARTISTS_ROOT"
    const val ALBUMS_ROOT = "ALBUMS_ROOT"
    const val GENRES_ROOT = "GENRES_ROOT"
    const val PLAYLISTS_ROOT = "PLAYLISTS_ROOT"
    const val PLAYLIST = "PLAYLIST"
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
    const val TRACK_LIST_ID = "trackListId"
    const val ALBUM_ID = "albumId"
    const val ARTIST_ID = "artistName"
    const val PLAYLIST_ID = "playlistId"
    const val MEDIA_ID = "mediaId"
    const val MEDIA_TYPE = "mediaType"
    const val MEDIA_GROUP_ID = "mediaGroupId"
    const val MEDIA_GROUP_TYPE = "mediaGroupType"
    const val TRACKS_LIST_TYPE = "trackListType"
    const val SORTABLE_LIST_TYPE = "sortableListType"
    const val TRACK_INDEX = "trackIndex"
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

inline fun <reified T : Parcelable> NavController.navigateTo(route: String, arguments: T) {
    val encodedArgs = Uri.encode(Json.encodeToString(arguments))
    val navRoute = "$route/$encodedArgs"
    navigate(navRoute)
}


inline fun <reified T : Parcelable> getArgumentsType(): NavType<T> = object : NavType<T>(false) {
    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): T {
        return bundle.getParcelable(key)!!
    }

    override fun parseValue(value: String): T {
        return Json.decodeFromString(value)
    }


}

inline fun <reified VM : ViewModel, reified T : Parcelable> NavGraphBuilder.screenDestination(
    name: String,
    crossinline screen: @Composable (VM) -> Unit
) {
    composable(
        route = "$name/{$ARGS}",
        arguments = listOf(navArgument(ARGS) {
            type = getArgumentsType<T>()
        })
    ) {
        val screenViewModel = hiltViewModel<VM>()
        screen(screenViewModel)
    }
}