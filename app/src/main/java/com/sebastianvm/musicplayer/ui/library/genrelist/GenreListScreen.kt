package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments

@Composable
fun GenreListLayout(
    state: GenreListState,
    navigateToGenre: (TrackListArguments) -> Unit,
    openGenreContextMenu: (GenreContextMenuArguments) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.genreList.isEmpty()) {
        StoragePermissionNeededEmptyScreen(
            message = R.string.no_genres_found,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        )
    } else {
        LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
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
}
