package com.sebastianvm.musicplayer.ui.library.genres

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTopBar
import com.sebastianvm.musicplayer.ui.components.LibraryTopBarDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface GenresListScreenNavigationDelegate {
    fun navigateUp()
    fun navigateToGenre(genreName: String)
}

@Composable
fun GenresListScreen(
    screenViewModel: GenresListViewModel = viewModel(),
    delegate: GenresListScreenNavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is GenresListUiEvent.NavigateToGenre -> {
                    delegate.navigateToGenre(event.genreName)
                }
                GenresListUiEvent.NavigateUp -> delegate.navigateUp()
            }
        },
        topBar = {
            LibraryTopBar(
                title = DisplayableString.ResourceValue(R.string.genres),
                delegate = object : LibraryTopBarDelegate {
                    override fun sortByClicked() {
                        screenViewModel.handle(GenresListUserAction.SortByClicked)
                    }

                    override fun upButtonClicked() {
                        screenViewModel.handle(GenresListUserAction.UpButtonClicked)
                    }
                })
        }) { state ->
        GenresListLayout(state = state, object : GenresListScreenDelegate {
            override fun onGenreClicked(genreName: String) {
                screenViewModel.handle(action = GenresListUserAction.GenreClicked(genreName = genreName))
            }
        })
    }
}

interface GenresListScreenDelegate {
    fun onGenreClicked(genreName: String)
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GenresListScreenPreview(
    @PreviewParameter(GenresListStatePreviewParameterProvider::class) state: GenresListState
) {
    ScreenPreview {
        GenresListLayout(state = state, object : GenresListScreenDelegate {
            override fun onGenreClicked(genreName: String) = Unit
        })
    }
}


@Composable
fun GenresListLayout(
    state: GenresListState,
    delegate: GenresListScreenDelegate
) {
    LazyColumn {
        items(state.genresList) { item ->
            Text(
                modifier = Modifier
                    .clickable {
                        delegate.onGenreClicked(item.genreName)
                    }
                    .fillMaxWidth()
                    .padding(
                        horizontal = AppDimensions.spacing.mediumLarge,
                        vertical = AppDimensions.spacing.small
                    ),
                text = item.genreName,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

