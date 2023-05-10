package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavFunction

@Composable
fun GenreListLayout(
    state: GenreListState,
    navigateToGenre: NavFunction<TrackListArguments>,
    openGenreContextMenu: NavFunction<GenreContextMenuArguments>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(state.genreList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    navigateToGenre(
                        TrackListArguments(trackList = MediaGroup.Genre(genreId = item.id))
                    )

                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            openGenreContextMenu(GenreContextMenuArguments(genreId = item.id))
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(id = R.string.more)
                        )
                    }
                }
            )
        }
    }
}
