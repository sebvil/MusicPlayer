package com.sebastianvm.musicplayer.ui.album

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItem
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface

@Composable
fun AlbumScreen(
    screenViewModel: AlbumViewModel,
    navigationDelegate: NavigationDelegate,
) {
    Screen(
        screenViewModel = screenViewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) {
        AlbumLayout(viewModel = screenViewModel)
    }
}

@ScreenPreview
@Composable
fun AlbumScreenPreview(
    @PreviewParameter(AlbumStatePreviewParameterProvider::class) state: AlbumState
) {
    ScreenPreview {
        AlbumLayout(viewModel = DefaultViewModelInterfaceProvider.getDefaultInstance(state))
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumLayout(viewModel: ViewModelInterface<AlbumState, AlbumUserAction>) {
    val state by viewModel.state.collectAsState()
    LazyColumn {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppDimensions.spacing.medium),
            ) {
                MediaArtImage(
                    uri = state.imageUri,
                    contentDescription = stringResource(
                        id = R.string.album_art_for_album,
                        state.albumName
                    ),
                    backupContentDescription = R.string.placeholder_album_art,
                    backupResource = R.drawable.ic_album,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(all = AppDimensions.spacing.mediumLarge),
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    text = state.albumName,
                    modifier = Modifier.padding(horizontal = AppDimensions.spacing.mediumLarge),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        itemsIndexed(state.trackList) { index, item ->
            ModelListItem(
                state = item,
                modifier = Modifier
                    .animateItemPlacement()
                    .clickable {
                        viewModel.handle(AlbumUserAction.TrackClicked(index))
                    },
                trailingContent = {
                    IconButton(
                        onClick = {
                            viewModel.handle(
                                AlbumUserAction.TrackOverflowMenuIconClicked(
                                    index,
                                    item.id
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_overflow),
                            contentDescription = stringResource(R.string.more),
                        )
                    }
                }
            )
        }

    }
}
