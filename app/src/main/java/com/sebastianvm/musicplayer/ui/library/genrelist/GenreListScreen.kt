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
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun GenreListScreen(
    state: GenreListState,
    screenDelegate: ScreenDelegate<GenreListUserAction> = DefaultViewModelInterfaceProvider.getDefaultInstance()
) {
    ScreenLayout(
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.genres),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenDelegate.handle(GenreListUserAction.SortByButtonClicked)
                    }

                    override fun upButtonClicked() {
                        screenDelegate.handle(GenreListUserAction.UpButtonClicked)
                    }
                })
        }) {
        GenreListLayout(state, screenDelegate)
    }
}

@Composable
fun GenreListLayout(state: GenreListState, screenDelegate: ScreenDelegate<GenreListUserAction>) {
    LazyColumn {
        items(state.genreList) { item ->
            ModelListItem(
                state = item,
                modifier = Modifier.clickable {
                    screenDelegate.handle(
                        GenreListUserAction.GenreRowClicked(
                            item.id
                        )
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            screenDelegate.handle(
                                GenreListUserAction.GenreOverflowMenuIconClicked(
                                    item.id
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = com.sebastianvm.commons.R.drawable.ic_overflow),
                            contentDescription = stringResource(id = com.sebastianvm.commons.R.string.more)
                        )
                    }
                }
            )
        }
    }
}
