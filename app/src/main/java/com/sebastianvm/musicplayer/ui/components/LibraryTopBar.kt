package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.StringPreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


interface LibraryTopBarDelegate {
    fun upButtonClicked() = Unit
    fun sortByClicked() = Unit
}

data class LibraryTopBarState(val title: String, val hasSortButton: Boolean = true)

@Composable
fun LibraryTopBar(title: String, delegate: LibraryTopBarDelegate, titleAlpha: Float = 1f) {
    LibraryTopBar(
        state = LibraryTopBarState(title = title, hasSortButton = true),
        delegate = delegate,
        titleAlpha = titleAlpha
    )
}

// TODO Need to handle arbitrarily long text
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopBar(
    state: LibraryTopBarState,
    delegate: LibraryTopBarDelegate,
    titleAlpha: Float = 1f
) {
    TopAppBar(
        title = {
            Text(text = state.title, modifier = Modifier.alpha(titleAlpha))
        },
        navigationIcon = {
            IconButton(onClick = { delegate.upButtonClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        actions = {
            if (state.hasSortButton) {
                IconButton(onClick = { delegate.sortByClicked() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sort),
                        contentDescription = stringResource(id = R.string.sort_by)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreview(@PreviewParameter(StringPreviewParameterProvider::class) title: String) {
    ThemedPreview {
        LibraryTopBar(
            state = LibraryTopBarState(title = title, hasSortButton = true),
            delegate = object : LibraryTopBarDelegate {})
    }
}
