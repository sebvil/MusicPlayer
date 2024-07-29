package com.sebastianvm.musicplayer.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Empty
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState

@Composable
fun <S> UiStateScreen(
    uiState: UiState<S>,
    emptyScreen: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (S) -> Unit,
) {
    Box(modifier = modifier) {
        when (uiState) {
            is Data -> {
                content(uiState.state)
            }
            is Empty -> {
                emptyScreen()
            }
            is Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
