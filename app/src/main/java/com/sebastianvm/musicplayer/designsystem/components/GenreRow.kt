package com.sebastianvm.musicplayer.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.model.BasicGenre
import com.sebastianvm.model.Genre

object GenreRow {
    data class State(val id: Long, val genreName: String) {
        companion object {
            fun fromGenre(genre: Genre): State {
                return State(id = genre.id, genreName = genre.name)
            }

            fun fromGenre(genre: BasicGenre): State {
                return State(id = genre.id, genreName = genre.name)
            }
        }
    }
}

@Composable
fun GenreRow(
    state: GenreRow.State,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(text = state.genreName) },
        modifier = modifier,
        trailingContent = trailingContent,
    )
}
