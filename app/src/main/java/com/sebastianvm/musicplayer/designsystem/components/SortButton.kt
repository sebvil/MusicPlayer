package com.sebastianvm.musicplayer.designsystem.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

object SortButton {
    data class State(@StringRes val text: Int, val sortOrder: MediaSortOrder)
}

@Composable
fun SortButton(state: SortButton.State, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = "${stringResource(id = R.string.sort_by)}:")
        Icon(
            imageVector =
                if (state.sortOrder == MediaSortOrder.ASCENDING) Icons.Default.ArrowUpward
                else Icons.Default.ArrowDownward,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Text(text = stringResource(id = state.text))
    }
}
