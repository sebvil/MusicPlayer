package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.database.entities.Genre

object GenreRow {
    data class State(val id: Long, val genreName: String) {
        companion object {
            fun fromGenre(genre: Genre): State {
                return State(id = genre.id, genreName = genre.genreName)
            }
        }
    }
}

@Composable
fun GenreRow(state: GenreRow.State, onMoreIconClicked: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(text = state.genreName) },
        modifier = modifier,
        trailingContent = { OverflowIconButton(onClick = onMoreIconClicked) },
    )
}
