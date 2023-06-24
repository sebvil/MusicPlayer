package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.toSortableListType
import com.sebastianvm.musicplayer.ui.LocalPaddingValues
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.components.StoragePermissionNeededEmptyScreen
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem


@Composable
fun ArtistListLayout(
    state: ArtistListState,
    openArtistContextMenu: (ArtistContextMenuArguments) -> Unit,
    navigateToArtistScreen: (ArtistArguments) -> Unit,
    openSortMenu: (SortMenuArguments) -> Unit
    modifier: Modifier = Modifier,
) {
    if (state.artistList.isEmpty()) {
        StoragePermissionNeededEmptyScreen(
            message = R.string.no_artists_found,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        )
    } else {
        LazyColumn(modifier = modifier, contentPadding = LocalPaddingValues.current) {
            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.sort_by))
                    },
                    leadingContent = {
                        Icon(imageVector = Icons.Default.Sort, contentDescription = null)
                    },
                    modifier = Modifier.clickable {
                        openSortMenu(
                            SortMenuArguments(listType = Medi)
                        )
                    }
                )
            }
            items(state.artistList) { item ->
                ModelListItem(
                    state = item,
                    modifier = Modifier.clickable {
                        navigateToArtistScreen(ArtistArguments(item.id))
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                openArtistContextMenu(ArtistContextMenuArguments(artistId = item.id))
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
