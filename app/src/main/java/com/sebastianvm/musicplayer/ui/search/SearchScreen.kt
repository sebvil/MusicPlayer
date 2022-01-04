package com.sebastianvm.musicplayer.ui.search

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.material.TextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.TrackRow
import com.sebastianvm.musicplayer.ui.components.chip.SingleSelectFilterChipGroup
import com.sebastianvm.musicplayer.ui.theme.textFieldColors
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun SearchScreen(
    screenViewModel: SearchViewModel = viewModel(),
) {
    Screen(screenViewModel = screenViewModel, eventHandler = {}) { state ->
        SearchLayout(state = state, object : SearchScreenDelegate {
            override fun onTextChanged(newText: String) {
                screenViewModel.handle(SearchUserAction.OnTextChanged(newText = newText))
            }

            override fun onOptionChosen(@StringRes newOption: Int) {
                screenViewModel.handle(SearchUserAction.SearchTypeChanged(newType = newOption))
            }
        })
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchScreenPreview(@PreviewParameter(SearchStatePreviewParameterProvider::class) state: SearchState) {
    ScreenPreview {
        SearchLayout(state = state)
    }
}

interface SearchScreenDelegate {
    fun onTextChanged(newText: String) = Unit
    fun onOptionChosen(@StringRes newOption: Int) = Unit
}

@Composable
fun SearchLayout(
    state: SearchState,
    delegate: SearchScreenDelegate = object : SearchScreenDelegate {},
) {
    val context = LocalContext.current
    Column {
        TextField(
            value = state.searchTerm,
            onValueChange = delegate::onTextChanged,
            textStyle = LocalTextStyle.current,
            placeholder = {
                Text(
                    text = stringResource(R.string.search),
                    style = LocalTextStyle.current,
                    color = LocalContentColor.current
                )
            },
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        SingleSelectFilterChipGroup(
            options = listOf(R.string.songs, R.string.artists, R.string.albums, R.string.genres),
            selectedOption = state.selectedOption,
            modifier = Modifier.padding(vertical = AppDimensions.spacing.medium),
            getDisplayName = { ResUtil.getString(context, this) },
            onNewOptionSelected = { newOption -> delegate.onOptionChosen(newOption) }
        )
        LazyColumn {
            items(state.trackSearchResults) { item ->
                TrackRow(state = item) {}
            }
        }
    }
}

