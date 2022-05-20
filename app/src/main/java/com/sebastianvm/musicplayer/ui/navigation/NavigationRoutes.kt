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
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ContextMenuArguments
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
    const val PLAYER = "PLAYER"
    const val SEARCH = "SEARCH"
    const val SORT = "SORT"
    const val CONTEXT = "CONTEXT"
    const val MEDIA_ARTISTS = "MEDIA_ARTISTS"
    const val QUEUE = "QUEUE"
}

enum class NavigationRoute(val hasArgs: Boolean) {
    Library(hasArgs = false),
    Player(hasArgs = false),
    LibraryRoot(hasArgs = false),
    ArtistsRoot(hasArgs = false),
    AlbumsRoot(hasArgs = false),
    GenresRoot(hasArgs = false),
    PlaylistsRoot(hasArgs = false),
    Artist(hasArgs = true),
    Album(hasArgs = true),
    Playlist(hasArgs = true),
    TrackList(hasArgs = true),
    TrackSearch(hasArgs = true),
    ContextMenu(hasArgs = true)
}


interface NavigationArguments : Parcelable

sealed class NavigationDestination(
    val navigationRoute: NavigationRoute, open val arguments: NavigationArguments?
) {
    object MusicPlayer : NavigationDestination(NavigationRoute.Player, arguments = null)
    object ArtistsRoot : NavigationDestination(NavigationRoute.ArtistsRoot, arguments = null)
    object AlbumsRoot : NavigationDestination(NavigationRoute.AlbumsRoot, arguments = null)
    object GenresRoot : NavigationDestination(NavigationRoute.GenresRoot, arguments = null)
    object PlaylistsRoot : NavigationDestination(NavigationRoute.PlaylistsRoot, arguments = null)
    data class TrackList(override val arguments: TrackListArguments) :
        NavigationDestination(NavigationRoute.TrackList, arguments = arguments)

    data class AlbumDestination(override val arguments: AlbumArguments) :
        NavigationDestination(NavigationRoute.Album, arguments)


    data class PlaylistDestination(override val arguments: PlaylistArguments) :
        NavigationDestination(NavigationRoute.Playlist, arguments)

    data class TrackSearchDestination(override val arguments: TrackSearchArguments) :
        NavigationDestination(NavigationRoute.TrackSearch, arguments)

    data class ArtistDestination(override val arguments: ArtistArguments) :
        NavigationDestination(NavigationRoute.Artist, arguments = arguments)

    data class ContextMenu(override val arguments: ContextMenuArguments) :
        NavigationDestination(NavigationRoute.ContextMenu, arguments = arguments)

}

object NavArgs {
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
        subclass(ArtistArguments::class)
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
    destination: NavigationDestination, builder: NavOptionsBuilder.() -> Unit = {}
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

enum class DestinationType { Screen, BottomSheet }

@OptIn(ExperimentalMaterialNavigationApi::class)
inline fun <reified VM : ViewModel> NavGraphBuilder.screenDestination(
    destination: NavigationRoute,
    destinationType: DestinationType,
    crossinline screen: @Composable (VM) -> Unit
) {
    val route = if (destination.hasArgs) {
        "${destination.name}/args={$ARGS}"
    } else {
        destination.name
    }
    val args = if (destination.hasArgs) listOf(navArgument(ARGS) {
        type = getArgumentsType()
    }) else listOf()

    when (destinationType) {
        DestinationType.Screen -> composable(route = route, arguments = args) {
            val screenViewModel = hiltViewModel<VM>()
            screen(screenViewModel)
        }
        DestinationType.BottomSheet -> bottomSheet(route = route, arguments = args) {
            val screenViewModel = hiltViewModel<VM>()
            screen(screenViewModel)
        }
    }

}

