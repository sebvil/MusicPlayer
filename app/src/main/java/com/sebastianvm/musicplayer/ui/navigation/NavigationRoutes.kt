package com.sebastianvm.musicplayer.ui.navigation

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
import com.sebastianvm.musicplayer.ui.playlist.PlaylistArguments
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

const val ARGS = "ARGS"

object NavRoutes {
    const val ARTIST = "ARTIST"
    const val PLAYER = "PLAYER"
    const val SEARCH = "SEARCH"
    const val SORT = "SORT"
    const val CONTEXT = "CONTEXT"
    const val MEDIA_ARTISTS = "MEDIA_ARTISTS"
    const val QUEUE = "QUEUE"
}

enum class NavigationRoute(val hasArgs: Boolean) {
    Library(hasArgs = false),
    LibraryRoot(hasArgs = false),
    ArtistsRoot(hasArgs = false),
    AlbumsRoot(hasArgs = false),
    GenresRoot(hasArgs = false),
    PlaylistsRoot(hasArgs = false),
    Album(hasArgs = true),
    TrackSearch(hasArgs = true),
    Playlist(hasArgs = true),
    Player(hasArgs = false),
    TrackList(hasArgs = true),
}


interface NavigationArguments : Parcelable

sealed class NavigationDestination(
    val navigationRoute: NavigationRoute, open val arguments: NavigationArguments?
) {
    data class PlaylistDestination(override val arguments: PlaylistArguments) :
        NavigationDestination(NavigationRoute.Playlist, arguments)

    data class TrackSearchDestination(override val arguments: TrackSearchArguments) :
        NavigationDestination(NavigationRoute.TrackSearch, arguments)

    object MusicPlayerDestination : NavigationDestination(NavigationRoute.Player, arguments = null)

    data class AlbumDestination(override val arguments: AlbumArguments) :
        NavigationDestination(NavigationRoute.Album, arguments)

    data class TrackListDestination(override val arguments: TrackListArguments) :
        NavigationDestination(NavigationRoute.TrackList, arguments = arguments)

    object ArtistsRoot : NavigationDestination(NavigationRoute.ArtistsRoot, arguments = null)
    object AlbumsRoot : NavigationDestination(NavigationRoute.AlbumsRoot, arguments = null)
    object GenresRoot : NavigationDestination(NavigationRoute.GenresRoot, arguments = null)
    object PlaylistsRoot : NavigationDestination(NavigationRoute.PlaylistsRoot, arguments = null)
}

object NavArgs {
    const val ARTIST_ID = "artistName"
    const val MEDIA_ID = "mediaId"
    const val MEDIA_TYPE = "mediaType"
    const val MEDIA_GROUP_ID = "mediaGroupId"
    const val MEDIA_GROUP_TYPE = "mediaGroupType"
    const val SORTABLE_LIST_TYPE = "sortableListType"
    const val TRACK_INDEX = "trackIndex"
}


private val module = SerializersModule {
    polymorphic(NavigationArguments::class) {
        subclass(PlaylistArguments::class)
        subclass(TrackSearchArguments::class)
        subclass(AlbumArguments::class)
        subclass(TrackListArguments::class)
    }
}

private val json = Json { serializersModule = module }

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


fun NavController.navigateTo(
    destination: NavigationDestination,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    val navRoute = if (destination.navigationRoute.hasArgs) {
        val encodedArgs = Uri.encode(json.encodeToString(destination.arguments))
        "${destination.navigationRoute.name}/args=$encodedArgs"
    } else {
        destination.navigationRoute.name
    }
    navigate(navRoute, builder)
}


fun getArgumentsType(): NavType<NavigationArguments> =
    object : NavType<NavigationArguments>(false) {
        override fun put(bundle: Bundle, key: String, value: NavigationArguments) {
            bundle.putParcelable(key, value)
        }

        override fun get(bundle: Bundle, key: String): NavigationArguments {
            return bundle.getParcelable(key)!!
        }

        override fun parseValue(value: String): NavigationArguments {
            return json.decodeFromString(value)
        }
    }

inline fun <reified VM : ViewModel> NavGraphBuilder.screenDestination(
    destination: NavigationRoute, crossinline screen: @Composable (VM) -> Unit
) {
    val route = if (destination.hasArgs) {
        "${destination.name}/args={$ARGS}"
    } else {
        destination.name
    }
    val args = if (destination.hasArgs) listOf(navArgument(ARGS) {
        type = getArgumentsType()
    }) else listOf()
    composable(route = route, arguments = args) {
        val screenViewModel = hiltViewModel<VM>()
        screen(screenViewModel)
    }
}

