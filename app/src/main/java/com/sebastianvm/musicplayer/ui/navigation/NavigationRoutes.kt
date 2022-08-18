package com.sebastianvm.musicplayer.ui.navigation

import android.net.Uri
import android.os.Build
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
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
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

enum class NavigationRoute(val hasArgs: Boolean) {
    Library(hasArgs = false),
    Player(hasArgs = false),
    Queue(hasArgs = false),
    Search(hasArgs = false),
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
    TrackContextMenu(hasArgs = true),
    ArtistContextMenu(hasArgs = true),
    AlbumContextMenu(hasArgs = true),
    GenreContextMenu(hasArgs = true),
    PlaylistContextMenu(hasArgs = true),
    SortMenu(hasArgs = true),
    ArtistsMenu(hasArgs = true)
}


interface NavigationArguments : Parcelable

sealed class NavigationDestination(
    val navigationRoute: NavigationRoute,
    open val arguments: NavigationArguments?,
    val isBottomNavDestination: Boolean = false
) {
    object MusicPlayer : NavigationDestination(
        NavigationRoute.Player,
        arguments = null,
        isBottomNavDestination = true
    )

    object Search : NavigationDestination(
        NavigationRoute.Search,
        arguments = null,
        isBottomNavDestination = false
    )

    object Queue : NavigationDestination(
        NavigationRoute.Queue,
        arguments = null,
        isBottomNavDestination = true
    )

    object LibraryRoot : NavigationDestination(
        NavigationRoute.Library,
        arguments = null,
        isBottomNavDestination = true
    )

    object ArtistsRoot : NavigationDestination(NavigationRoute.ArtistsRoot, arguments = null)
    object AlbumsRoot : NavigationDestination(NavigationRoute.AlbumsRoot, arguments = null)
    object GenresRoot : NavigationDestination(NavigationRoute.GenresRoot, arguments = null)
    object PlaylistsRoot : NavigationDestination(NavigationRoute.PlaylistsRoot, arguments = null)
    data class TrackList(override val arguments: TrackListArguments) :
        NavigationDestination(NavigationRoute.TrackList, arguments = arguments)

    data class Album(override val arguments: AlbumArguments) :
        NavigationDestination(NavigationRoute.Album, arguments)


    data class Playlist(override val arguments: PlaylistArguments) :
        NavigationDestination(NavigationRoute.Playlist, arguments)

    data class TrackSearch(override val arguments: TrackSearchArguments) :
        NavigationDestination(NavigationRoute.TrackSearch, arguments)

    data class Artist(override val arguments: ArtistArguments) :
        NavigationDestination(NavigationRoute.Artist, arguments = arguments)

    data class TrackContextMenu(override val arguments: TrackContextMenuArguments) :
        NavigationDestination(NavigationRoute.TrackContextMenu, arguments = arguments)

    data class ArtistContextMenu(override val arguments: ArtistContextMenuArguments) :
        NavigationDestination(NavigationRoute.ArtistContextMenu, arguments = arguments)

    data class AlbumContextMenu(override val arguments: AlbumContextMenuArguments) :
        NavigationDestination(NavigationRoute.AlbumContextMenu, arguments = arguments)

    data class GenreContextMenu(override val arguments: GenreContextMenuArguments) :
        NavigationDestination(NavigationRoute.GenreContextMenu, arguments = arguments)

    data class PlaylistContextMenu(override val arguments: PlaylistContextMenuArguments) :
        NavigationDestination(NavigationRoute.PlaylistContextMenu, arguments = arguments)

    data class SortMenu(override val arguments: SortMenuArguments) :
        NavigationDestination(NavigationRoute.SortMenu, arguments = arguments)

    data class ArtistsMenu(override val arguments: ArtistsMenuArguments) :
        NavigationDestination(NavigationRoute.ArtistsMenu, arguments = arguments)

}


private val module = SerializersModule {
    polymorphic(NavigationArguments::class) {
        subclass(PlaylistArguments::class)
        subclass(TrackSearchArguments::class)
        subclass(AlbumArguments::class)
        subclass(TrackListArguments::class)
        subclass(ArtistArguments::class)
        subclass(TrackContextMenuArguments::class)
        subclass(ArtistContextMenuArguments::class)
        subclass(AlbumContextMenuArguments::class)
        subclass(GenreContextMenuArguments::class)
        subclass(PlaylistContextMenuArguments::class)
        subclass(SortMenuArguments::class)
        subclass(ArtistsMenuArguments::class)
    }
}

private val json = Json { serializersModule = module }

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
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, NavigationArguments::class.java)!!
            } else {
                @Suppress("deprecation")
                bundle.getParcelable(key)!!
            }
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

