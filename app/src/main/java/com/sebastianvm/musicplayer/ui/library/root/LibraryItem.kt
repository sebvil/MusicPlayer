package com.sebastianvm.musicplayer.ui.library.root

import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.jayasuryat.dowel.annotation.Dowel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListViewModel
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination

sealed class LibraryItem(
    val destination: NavigationDestination,
    @StringRes val rowName: Int,
    @DrawableRes val icon: Int,
    @PluralsRes val countString: Int,
    open val count: Int
) {

    @Dowel
    data class Tracks(override val count: Int) : LibraryItem(
        destination = NavigationDestination.TrackList(
            TrackListArguments(
                trackListType = TrackListType.ALL_TRACKS,
                trackListId = TrackListViewModel.ALL_TRACKS
            )
        ),
        rowName = R.string.all_songs,
        icon = R.drawable.ic_song,
        countString = R.plurals.number_of_tracks,
        count = count
    )

    @Dowel
    data class Artists(override val count: Int) : LibraryItem(
        destination = NavigationDestination.ArtistsRoot,
        rowName = R.string.artists,
        icon = R.drawable.ic_artist,
        countString = R.plurals.number_of_artists,
        count = count
    )

    @Dowel
    data class Albums(override val count: Int) : LibraryItem(
        destination = NavigationDestination.AlbumsRoot,
        rowName = R.string.albums,
        icon = R.drawable.ic_album,
        countString = R.plurals.number_of_albums,
        count = count
    )

    @Dowel
    data class Genres(override val count: Int) : LibraryItem(
        destination = NavigationDestination.GenresRoot,
        rowName = R.string.genres,
        icon = R.drawable.ic_genre,
        countString = R.plurals.number_of_genres,
        count = count
    )

    @Dowel
    data class Playlists(override val count: Int) : LibraryItem(
        destination = NavigationDestination.PlaylistsRoot,
        rowName = R.string.playlists,
        icon = R.drawable.ic_playlist,
        countString = R.plurals.number_of_playlists,
        count = count
    )
}
