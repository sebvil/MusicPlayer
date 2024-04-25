package com.sebastianvm.musicplayer

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.ui.MainViewModel
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.artist.ArtistViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsBottomSheetViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.ArtistsMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortBottomSheetViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.library.albumlist.AlbumListViewModel
import com.sebastianvm.musicplayer.ui.library.artistlist.ArtistListViewModel
import com.sebastianvm.musicplayer.ui.library.genrelist.GenreListViewModel
import com.sebastianvm.musicplayer.ui.library.playlistlist.PlaylistListViewModel
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListViewModel
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchViewModel
import com.sebastianvm.musicplayer.ui.search.SearchViewModel

class MusicPlayerApplication : Application() {

    val dependencyContainer by lazy { DependencyContainer(this) }

    val viewModelFactory: AbstractSavedStateViewModelFactory =
        object : AbstractSavedStateViewModelFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                when (modelClass) {
                    ArtistViewModel::class.java -> {
                        val args = handle.navArgs<ArtistArguments>()
                        return ArtistViewModel(
                            arguments = args,
                            artistRepository = dependencyContainer.repositoryProvider.artistRepository
                        ) as T
                    }

                    AlbumContextMenuViewModel::class.java -> {
                        val args = handle.navArgs<AlbumContextMenuArguments>()
                        return AlbumContextMenuViewModel(
                            arguments = args,
                            albumRepository = dependencyContainer.repositoryProvider.albumRepository,
                            playbackManager = dependencyContainer.repositoryProvider.playbackManager
                        ) as T
                    }

                    ArtistContextMenuViewModel::class.java -> {
                        val args = handle.navArgs<ArtistContextMenuArguments>()
                        return ArtistContextMenuViewModel(
                            arguments = args,
                            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
                        ) as T
                    }

                    GenreContextMenuViewModel::class.java -> {
                        val args = handle.navArgs<GenreContextMenuArguments>()
                        return GenreContextMenuViewModel(
                            arguments = args,
                            genreRepository = dependencyContainer.repositoryProvider.genreRepository,
                        ) as T
                    }

                    PlaylistContextMenuViewModel::class.java -> {
                        val args = handle.navArgs<PlaylistContextMenuArguments>()
                        return PlaylistContextMenuViewModel(
                            arguments = args,
                            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
                        ) as T
                    }

                    TrackContextMenuViewModel::class.java -> {
                        val args = handle.navArgs<TrackContextMenuArguments>()
                        return TrackContextMenuViewModel(
                            arguments = args,
                            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
                            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
                            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
                        ) as T
                    }

                    ArtistsBottomSheetViewModel::class.java -> {
                        val args = handle.navArgs<ArtistsMenuArguments>()
                        return ArtistsBottomSheetViewModel(
                            arguments = args,
                            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
                        ) as T
                    }

                    SortBottomSheetViewModel::class.java -> {
                        val args = handle.navArgs<SortMenuArguments>()
                        return SortBottomSheetViewModel(
                            arguments = args,
                            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
                        ) as T
                    }

                    TrackSearchViewModel::class.java -> {
                        val args = handle.navArgs<TrackSearchArguments>()
                        return TrackSearchViewModel(
                            arguments = args,
                            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
                            ftsRepository = dependencyContainer.repositoryProvider.searchRepository,
                        ) as T
                    }

                    SearchViewModel::class.java -> {
                        return SearchViewModel(
                            ftsRepository = dependencyContainer.repositoryProvider.searchRepository,
                            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
                            defaultDispatcher = dependencyContainer.dispatcherProvider.defaultDispatcher
                        ) as T
                    }

                    AlbumListViewModel::class.java -> {
                        return AlbumListViewModel(
                            albumRepository = dependencyContainer.repositoryProvider.albumRepository,
                            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
                        ) as T
                    }

                    ArtistListViewModel::class.java -> {
                        return ArtistListViewModel(
                            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
                            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
                        ) as T
                    }

                    GenreListViewModel::class.java -> {
                        return GenreListViewModel(
                            genreRepository = dependencyContainer.repositoryProvider.genreRepository,
                            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
                        ) as T
                    }

                    PlaylistListViewModel::class.java -> {
                        return PlaylistListViewModel(
                            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
                            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
                        ) as T
                    }

                    TrackListViewModel::class.java -> {
                        val args = handle.navArgs<TrackListArgumentsForNav>()
                        return TrackListViewModel(
                            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
                            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
                            args = args.toTrackListArguments()
                        ) as T
                    }

                    MainViewModel::class.java -> {
                        return MainViewModel(
                            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
                        ) as T
                    }

                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
}
