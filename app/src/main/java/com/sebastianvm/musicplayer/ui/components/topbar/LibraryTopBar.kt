package com.sebastianvm.musicplayer.ui.components.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.sebastianvm.musicplayer.R

interface LibraryTopBarDelegate {
    fun upButtonClicked() = Unit
    fun sortByClicked() = Unit
}

data class LibraryTopBarState(val title: String)


// TODO remove sort button from here to not allow sorting unless at the top
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryTopBar(
    state: LibraryTopBarState,
    onUpButtonClicked: () -> Unit,
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
            IconButton(onClick = onUpButtonClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}