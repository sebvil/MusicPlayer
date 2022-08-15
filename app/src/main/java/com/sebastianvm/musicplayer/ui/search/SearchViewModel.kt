package com.sebastianvm.musicplayer.ui.search

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    initialState: SearchState,
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<SearchUiEvent, SearchState>(initialState) {

    private val searchTerm = MutableStateFlow("")

    init {
        setState {
            copy(
                trackSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchTracksPaged(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.track.toTrackRowState(includeTrackNumber = false) }
                    }
                },
                artistSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchArtists(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.toModelListItemState() }
                    }
                },
                albumSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchAlbums(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.album.toModelListItemState() }
                    }
                },
                genreSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchGenres(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.toModelListItemState() }
                    }
                },
            )

        }
    }

    fun onTextChanged(newText: String) {
        searchTerm.update { newText }
    }

    fun onSearchTypeChanged(newType: Int) {
        setState {
            copy(
                selectedOption = newType
            )
        }
    }

    fun onTrackRowClicked(trackId: Long) {
        viewModelScope.launch {
            playbackManager.playSingleTrack(trackId)
            addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
        }
    }

    fun onArtistRowClicked(artistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.Artist(
                    ArtistArguments(artistId = artistId)
                )
            )
        )
    }

    fun onAlbumRowClicked(albumId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.Album(
                    AlbumArguments(albumId = albumId)
                )
            )
        )
    }

    fun onGenreRowClicked(genreId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackList(
                    TrackListArguments(trackListType = TrackListType.GENRE, trackListId = genreId)
                )
            )
        )
    }

    fun onTrackOverflowMenuClicked(trackId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackContextMenu(
                    TrackContextMenuArguments(
                        trackId = trackId,
                        mediaType = MediaType.TRACK,
                        mediaGroup = MediaGroup(
                            mediaId = trackId,
                            mediaGroupType = MediaGroupType.SINGLE_TRACK
                        )
                    )
                )
            )
        )
    }

    fun onArtistOverflowMenuClicked(artistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.ArtistContextMenu(
                    ArtistContextMenuArguments(artistId = artistId)
                )
            )
        )
    }

    fun onAlbumOverflowMenuClicked(albumId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.AlbumContextMenu(
                    AlbumContextMenuArguments(albumId = albumId)
                )
            )
        )
    }

    fun onGenreOverflowMenuClicked(genreId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.GenreContextMenu(
                    GenreContextMenuArguments(genreId = genreId)
                )
            )
        )
    }
}

data class SearchState(
    @StringRes val selectedOption: Int,
    val trackSearchResults: Flow<PagingData<TrackRowState>>,
    val artistSearchResults: Flow<PagingData<ModelListItemState>>,
    val albumSearchResults: Flow<PagingData<ModelListItemState>>,
    val genreSearchResults: Flow<PagingData<ModelListItemState>>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialSearchStateProvider(): SearchState {
        return SearchState(
            selectedOption = R.string.songs,
            trackSearchResults = flow {},
            artistSearchResults = flow {},
            albumSearchResults = flow {},
            genreSearchResults = flow {},
        )
    }
}

sealed class SearchUiEvent : UiEvent