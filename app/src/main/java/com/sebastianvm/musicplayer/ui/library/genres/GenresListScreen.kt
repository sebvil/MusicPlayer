package com.sebastianvm.musicplayer.ui.library.genres

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.LibraryTitle
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

@Composable
fun GenresListScreen(
    screenViewModel: GenresListViewModel = viewModel(),
    bottomNavBar: @Composable () -> Unit,
    navigateToGenre: (String) -> Unit = {}
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is GenresListUiEvent.NavigateToGenre -> {
                    navigateToGenre(event.genreName)
                }
            }
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
    val listState = ListWithHeaderState(
        DisplayableString.ResourceValue(R.string.genres),
        state.genresList,
        { header -> LibraryTitle(title = header) },
        { item ->
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
    )
    ListWithHeader(state = listState)
}

