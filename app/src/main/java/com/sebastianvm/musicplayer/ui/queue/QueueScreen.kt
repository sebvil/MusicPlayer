package com.sebastianvm.musicplayer.ui.queue

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.LinearLayoutManager
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnList
import com.sebastianvm.musicplayer.ui.components.lists.DraggableColumnListDelegate
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate

@Composable
fun QueueScreen(screenViewModel: QueueViewModel, navigationDelegate: NavigationDelegate) {
    val layoutManager = LinearLayoutManager(LocalContext.current)
    Screen(
        screenViewModel = screenViewModel,
        navigationDelegate = navigationDelegate
    ) { state, delegate ->
        QueueLayout(
            state = state,
            screenDelegate = delegate,
            layoutManager = layoutManager
        )
    }
}

@Composable
fun QueueLayout(
    state: QueueState,
    screenDelegate: ScreenDelegate<QueueUserAction>,
    layoutManager: LinearLayoutManager
) {
    DraggableColumnList(
        items = state.queueItems,
        delegate = object : DraggableColumnListDelegate {
            override fun onItemSelectedForDrag(position: Int) {
                screenDelegate.handle(QueueUserAction.ItemSelectedForDrag(position))
            }

            override fun onDragEnded(initialPosition: Int, finalPosition: Int) {
                screenDelegate.handle(QueueUserAction.DragEnded(initialPosition, finalPosition))
            }

            override fun onMove(from: Int, to: Int) {
                screenDelegate.handle(QueueUserAction.ItemMoved(to))
            }
        },
        listAdapter = QueueAdapter { index, item ->
            val isNowPlayingItem = item.id == state.nowPlayingId
            val backgroundColor = if (isNowPlayingItem) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
            ModelListItem(
                state = item.modelListItemState,
                modifier = Modifier
                    .clickable {
                        screenDelegate.handle(QueueUserAction.TrackClicked(index))
                    },
                backgroundColor = backgroundColor,
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drag),
                        contentDescription = stringResource(R.string.drag),
                    )
                },
                trailingContent = {
                    IconButton(
                        onClick = { /* TODO */ },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more),
                        )
                    }
                })
        },
        layoutManager = layoutManager
    )
}
