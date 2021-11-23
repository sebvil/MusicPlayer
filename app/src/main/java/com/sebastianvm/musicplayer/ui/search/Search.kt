package com.sebastianvm.musicplayer.ui.search

import android.content.res.Configuration
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


@Composable
fun SearchScreen(
    screenViewModel: SearchViewModel = viewModel(),
) {
    val state = screenViewModel.state.observeAsState(screenViewModel.state.value)
    SearchLayout(state = state.value, object : SearchScreenDelegate {
        override fun onTextChanged(newText: String) {
            screenViewModel.handle(SearchUserAction.OnTextChanged(newText = newText))
        }
    })
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
            colors = M3TextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn {
            items(state.searchResults) { item ->
                Text(text = item)
            }
        }
    }
}


class M3TextFieldColors : TextFieldColors {

    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
    }

    @Composable
    override fun cursorColor(isError: Boolean): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.onSurfaceVariant)
    }

    @Composable
    override fun indicatorColor(
        enabled: Boolean,
        isError: Boolean,
        interactionSource: InteractionSource
    ): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.primary)
    }

    @Composable
    override fun labelColor(
        enabled: Boolean,
        error: Boolean,
        interactionSource: InteractionSource
    ): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.primary)
    }

    @Composable
    override fun leadingIconColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.onSurfaceVariant)
    }

    @Composable
    override fun placeholderColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    }

    @Composable
    override fun textColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.onSurfaceVariant)
    }

    @Composable
    override fun trailingIconColor(enabled: Boolean, isError: Boolean): State<Color> {
        return rememberUpdatedState(MaterialTheme.colorScheme.onSurfaceVariant)
    }

}
