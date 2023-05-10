package com.sebastianvm.musicplayer.ui.library.artistlist

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
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem

@Composable
fun ArtistListLayout(
    state: ArtistListState,
    openArtistContextMenu: (ArtistContextMenuArguments) -> Unit,
    navigateToArtistScreen: (ArtistArguments) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
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
