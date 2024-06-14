package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.features.navigation.LocalSharedTransitionScopes
import com.sebastianvm.musicplayer.features.navigation.SharedContentKey
import com.sebastianvm.musicplayer.model.AlbumWithArtists
import com.sebastianvm.musicplayer.ui.components.MediaArtImage

object AlbumRow {
    data class State(
        val id: Long,
        val albumName: String,
        val artists: String?,
        val artworkUri: String,
    ) {
        companion object {
            fun fromAlbum(album: AlbumWithArtists): State {
                return State(
                    id = album.id,
                    albumName = album.title,
                    artists = album.artists.joinToString { it.name },
                    artworkUri = album.imageUri,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AlbumRow(
    state: AlbumRow.State,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val sharedTransitionScopes = LocalSharedTransitionScopes.current

    with(sharedTransitionScopes.sharedTransitionScope) {
        ListItem(
            headlineContent = {
                Text(
                    text = state.albumName,
                    modifier =
                        Modifier.sharedBounds(
                            sharedContentState =
                                rememberSharedContentState(
                                    key = SharedContentKey.AlbumName(state.id)
                                ),
                            animatedVisibilityScope =
                                sharedTransitionScopes.animatedVisibilityScope,
                        )
                )
            },
            supportingContent = state.artists?.let { artists -> { Text(text = artists) } },
            modifier = modifier,
            leadingContent = {
                MediaArtImage(
                    artworkUri = state.artworkUri,
                    modifier =
                        Modifier.size(56.dp)
                            .sharedElement(
                                state =
                                    rememberSharedContentState(
                                        key = SharedContentKey.AlbumImage(state.id)
                                    ),
                                animatedVisibilityScope =
                                    sharedTransitionScopes.animatedVisibilityScope
                            ),
                )
            },
            trailingContent = trailingContent,
        )
    }
}
