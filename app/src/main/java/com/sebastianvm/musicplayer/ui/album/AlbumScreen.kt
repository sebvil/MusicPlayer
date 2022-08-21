package com.sebastianvm.musicplayer.ui.album

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.MediaArtImage
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.TrackList
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
        AlbumLayout(viewModel = screenViewModel, navigationDelegate = navigationDelegate)
    }
}

@ScreenPreview
@Composable
fun AlbumScreenPreview(
    @PreviewParameter(AlbumStatePreviewParameterProvider::class) state: AlbumState
) {
    ScreenPreview {
        AlbumLayout(
            viewModel = DefaultViewModelInterfaceProvider.getDefaultInstance(state),
            navigationDelegate = NavigationDelegate(rememberNavController())
        )
    }
}


@Composable
fun AlbumLayout(
    viewModel: ViewModelInterface<AlbumState, AlbumUserAction>,
    navigationDelegate: NavigationDelegate
) {
    val state by viewModel.state.collectAsState()

    TrackList(viewModel = hiltViewModel(), navigationDelegate = navigationDelegate) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppDimensions.spacing.medium)
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
}
