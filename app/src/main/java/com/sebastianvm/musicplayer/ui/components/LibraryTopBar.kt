package com.sebastianvm.musicplayer.ui.components

import android.content.res.Configuration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun LibraryTopBar(title: String, delegate: LibraryTopBarDelegate) {
    SmallTopAppBar(
        title = {
            Text(text = title)
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
            IconButton(onClick = { delegate.sortByClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = stringResource(id = R.string.sort_by)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreview(@PreviewParameter(StringPreviewParameterProvider::class) title: String) {
    ThemedPreview {
        LibraryTopBar(title = title, delegate = object : LibraryTopBarDelegate {})
    }
}
