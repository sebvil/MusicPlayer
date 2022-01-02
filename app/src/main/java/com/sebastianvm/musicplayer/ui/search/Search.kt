package com.sebastianvm.musicplayer.ui.search

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.material.TextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.ui.theme.textFieldColors
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
}

@Composable
fun SearchLayout(
    state: SearchState,
    delegate: SearchScreenDelegate = object : SearchScreenDelegate {},
) {
    Column {

        TextField(
            value = state.searchTerm,
            onValueChange = delegate::onTextChanged,
            textStyle = LocalTextStyle.current,
            placeholder = {
                Text(
                    text = "Search",
                    style = LocalTextStyle.current,
                    color = LocalContentColor.current
                )
            },
            colors = textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn {
            items(state.searchResults) { item ->
                Text(text = item)
            }
        }
    }
}

