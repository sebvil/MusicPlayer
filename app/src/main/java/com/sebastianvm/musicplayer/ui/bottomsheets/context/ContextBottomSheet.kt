package com.sebastianvm.musicplayer.ui.bottomsheets.context

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.BottomSheetPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.events.HandleEvents
import kotlinx.coroutines.Dispatchers


interface ContextBottomSheetDialogNavigationDelegate {
    fun navigateToPlayer() = Unit
    fun navigateToAlbum(albumId: String) = Unit
    fun navigateToArtist(artistName: String) = Unit
    fun navigateToArtistsBottomSheet(mediaId: String, mediaType: MediaType) = Unit
    fun navigateToGenre(genreName: String)
}

@Composable
fun <S : BaseContextMenuState> ContextBottomSheet(
    sheetViewModel: BaseContextMenuViewModel<S> = viewModel(),
    delegate: ContextBottomSheetDialogNavigationDelegate,
) {
    val state = sheetViewModel.state.collectAsState(context = Dispatchers.Main)
    HandleEvents(eventsFlow = sheetViewModel.eventsFlow) { event ->
        when (event) {
            is BaseContextMenuUiEvent.NavigateToPlayer -> {
                delegate.navigateToPlayer()
            }
            is BaseContextMenuUiEvent.NavigateToAlbum -> delegate.navigateToAlbum(event.albumId)
            is BaseContextMenuUiEvent.NavigateToArtist -> delegate.navigateToArtist(event.artistName)
            is BaseContextMenuUiEvent.NavigateToArtistsBottomSheet -> delegate.navigateToArtistsBottomSheet(
                event.mediaId,
                event.mediaType
            )
            is BaseContextMenuUiEvent.NavigateToGenre -> delegate.navigateToGenre(event.genreName)
        }
    }
    ContextMenuLayout(state = state.value, object : ContextMenuDelegate {
        override fun onRowClicked(contextMenuItem: ContextMenuItem) {
            sheetViewModel.handle(BaseContextMenuUserAction.RowClicked(contextMenuItem))
        }
    })
}

/**
 * The Android Studio Preview cannot handle this, but it can be run in device for preview
 */
@Preview
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ContextMenuScreenPreview(@PreviewParameter(ContextMenuStatePreviewParameterProvider::class) state: BaseContextMenuState) {
    BottomSheetPreview {
        ContextMenuLayout(state = state, object : ContextMenuDelegate {})
    }
}

interface ContextMenuDelegate {
    fun onRowClicked(contextMenuItem: ContextMenuItem) = Unit
}


@Composable
fun ContextMenuLayout(
    state: BaseContextMenuState,
    delegate: ContextMenuDelegate
) {
    with(state) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.bottomSheet.rowHeight)
                    .padding(start = AppDimensions.bottomSheet.startPadding)
            ) {
                Text(
                    text = state.menuTitle,
                    modifier = Modifier.paddingFromBaseline(top = 36.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
            LazyColumn {
                items(listItems, key = { it.text }) {
                    SingleLineListItem(
                        modifier = Modifier.clickable { delegate.onRowClicked(it) },
                        supportingImage = { iconModifier ->
                            Icon(
                                painter = painterResource(id = it.icon),
                                contentDescription = stringResource(id = it.text),
                                modifier = iconModifier,
                            )
                        },
                        supportingImageType = SupportingImageType.ICON,
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
}
