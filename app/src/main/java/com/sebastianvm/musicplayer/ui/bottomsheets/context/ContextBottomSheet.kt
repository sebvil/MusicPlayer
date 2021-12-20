package com.sebastianvm.musicplayer.ui.bottomsheets.context

import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.BottomSheetPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import kotlinx.coroutines.Dispatchers


interface ContextBottomSheetDialogNavigationDelegate {
    fun navigateToPlayer() = Unit
    fun navigateToAlbum(albumGid: String) = Unit
    fun navigateToArtist(artistGid: String) = Unit
    fun navigateToArtistsBottomSheet(mediaId: String, mediaType: MediaType) = Unit
}

@Composable
fun ContextBottomSheet(
    sheetViewModel: ContextMenuViewModel = viewModel(),
    delegate: ContextBottomSheetDialogNavigationDelegate,
) {
    val state = sheetViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(eventsFlow = sheetViewModel.eventsFlow) { event ->
        when (event) {
            is ContextMenuUiEvent.NavigateToPlayer -> {
                delegate.navigateToPlayer()
            }
            is ContextMenuUiEvent.NavigateToAlbum -> delegate.navigateToAlbum(event.albumGid)
            is ContextMenuUiEvent.NavigateToArtist -> delegate.navigateToArtist(event.artistGid)
            is ContextMenuUiEvent.NavigateToArtistsBottomSheet -> delegate.navigateToArtistsBottomSheet(
                event.mediaId,
                event.mediaType
            )
        }
    }
    ContextMenuLayout(state = state.value, object : ContextMenuDelegate {
        override fun onRowClicked(contextMenuItem: ContextMenuItem) {
            sheetViewModel.handle(ContextMenuUserAction.RowClicked(contextMenuItem))
        }
    })
}

/**
 * The Android Studio Preview cannot handle this, but it can be run in device for preview
 */
@Preview
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ContextMenuScreenPreview(@PreviewParameter(ContextMenuStatePreviewParameterProvider::class) state: ContextMenuState) {
    BottomSheetPreview {
        ContextMenuLayout(state = state, object : ContextMenuDelegate {})
    }
}

interface ContextMenuDelegate {
    fun onRowClicked(contextMenuItem: ContextMenuItem) = Unit
}


@Composable
fun ContextMenuLayout(
    state: ContextMenuState,
    delegate: ContextMenuDelegate
) {
    with(state) {
        LazyColumn {
            items(listItems, key = { it.text }) {
                SingleLineListItem(
                    supportingImage = { iconModifier ->
                        Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = DisplayableString.ResourceValue(it.text)
                                .getString(),
                            modifier = iconModifier,
                        )
                    },
                    supportingImageType = SupportingImageType.ICON,
                    delegate = object : ListItemDelegate {
                        override fun onItemClicked() {
                            delegate.onRowClicked(it)
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = it.text),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

