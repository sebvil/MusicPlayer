package com.sebastianvm.musicplayer.ui.components.topbar

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
import androidx.compose.ui.text.style.TextOverflow
import com.sebastianvm.musicplayer.R

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopBar(
    state: LibraryTopBarState,
    delegate: LibraryTopBarDelegate,
    titleAlpha: Float = 1f
) {
    TopAppBar(
        title = {
            Text(
                text = state.title,
                modifier = Modifier.alpha(titleAlpha),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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