package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface

@Composable
fun GenreListScreen(
    screenViewModel: GenreListViewModel = viewModel(),
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate,
        topBar = {
            LibraryTopBar(
                title = stringResource(id = R.string.genres),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenViewModel.handle(GenreListUserAction.SortByClicked)
                    }

                    override fun upButtonClicked() {
                        screenViewModel.handle(GenreListUserAction.UpButtonClicked)
                    }
                })
        }) {
        GenreListLayout(screenViewModel)
    }
}

@ScreenPreview
@Composable
fun GenreListScreenPreview(@PreviewParameter(GenreListStatePreviewParameterProvider::class) state: GenreListState) {
    ScreenPreview {
        GenreListLayout(viewModel = DefaultViewModelInterfaceProvider.getDefaultInstance(state))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreListLayout(viewModel: ViewModelInterface<GenreListState, GenreListUserAction>) {
    val state by viewModel.state.collectAsState()
    LazyColumn {
        items(state.genreList) { item ->
            ListItem(
                headlineText = {
                    Text(
                        text = item.genreName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                modifier = Modifier.clickable {
                    viewModel.handle(
                        GenreListUserAction.GenreRowClicked(
                            item.id
                        )
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            viewModel.handle(
                                GenreListUserAction.GenreOverflowMenuIconClicked(
                                    item.id
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more)
                        )
                    }
                },
            )
        }
    }
}
