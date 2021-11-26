package com.sebastianvm.musicplayer.ui.library.genres

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.extensions.MEDIA_METADATA_COMPAT_KEY
import com.sebastianvm.musicplayer.util.extensions.genre
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class GenresListViewModel @Inject constructor(
    musicServiceConnection: MusicServiceConnection,
    initialState: GenresListState
) :
    BaseViewModel<GenresListUserAction, GenresListUiEvent, GenresListState>(initialState) {

    init {
        musicServiceConnection.subscribe(BrowseTree.GENRES_ROOT,
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    setState {
                        copy(
                            genresList = children.mapNotNull { child ->
                                child.description.toGenreListItem()
                            }.sortedBy { item -> item.genreName },
                        )
                    }
                }
            })
    }

    fun MediaDescriptionCompat.toGenreListItem(): GenresListItem? {
        val meta =
            extras?.getParcelable<MediaMetadataCompat>(MEDIA_METADATA_COMPAT_KEY) ?: return null
        val genre = meta.genre ?: return null
        return GenresListItem(genre)
    }

    override fun handle(action: GenresListUserAction) {
        when (action) {
            is GenresListUserAction.GenreClicked -> {
                this.addUiEvent(GenresListUiEvent.NavigateToGenre(genreName = action.genreName))
            }
        }
    }
}

data class GenresListState(
    val genresList: List<GenresListItem>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialGenresListStateModule {

    @Provides
    @ViewModelScoped
    fun initialGenresListStateProvider() = GenresListState(genresList = listOf())
}

sealed class GenresListUserAction : UserAction {
    data class GenreClicked(val genreName: String) : GenresListUserAction()
}

sealed class GenresListUiEvent : UiEvent {
    data class NavigateToGenre(val genreName: String) : GenresListUiEvent()
}