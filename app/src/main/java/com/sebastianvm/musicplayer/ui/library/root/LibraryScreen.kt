package com.sebastianvm.musicplayer.ui.library.root

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat.startForegroundService
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.ui.components.PermissionHandler
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryItem
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryListItem
import com.sebastianvm.musicplayer.ui.library.root.searchbox.SearchBox
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout

@Composable
fun LibraryScreen(viewModel: LibraryViewModel, navigationDelegate: NavigationDelegate) {
    Screen(
        screenViewModel = viewModel,
        eventHandler = {},
        navigationDelegate = navigationDelegate
    ) { state, _ ->
        LibraryScreen(
            state = state,
            onLibraryItemClicked = { item: LibraryItem ->
                viewModel.handle(
                    LibraryUserAction.RowClicked(
                        item.destination
                    )
                )
            },
            onSearchBoxClicked = { viewModel.handle(LibraryUserAction.SearchBoxClicked) }
        )
    }
}


@Composable
fun LibraryScreen(
    state: LibraryState,
    onLibraryItemClicked: (item: LibraryItem) -> Unit = {},
    onSearchBoxClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    ScreenLayout(fab = {
        PermissionHandler(
            permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE,
            dialogTitle = R.string.storage_permission_needed,
            message = R.string.grant_storage_permissions,
            onPermissionGranted = {
                startForegroundService(
                    context,
                    Intent(context, LibraryScanService::class.java)
                )
            }
        ) { onClick ->
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = stringResource(id = R.string.scan),
                    )
                },
                onClick = onClick,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scan),
                        contentDescription = stringResource(id = R.string.scan)
                    )
                })
        }
    }) {
        LibraryLayout(
            state = state,
            onLibraryItemClicked = onLibraryItemClicked,
            onSearchBoxClicked = onSearchBoxClicked
        )
    }
}


@Composable
fun LibraryLayout(
    state: LibraryState,
    onLibraryItemClicked: (item: LibraryItem) -> Unit,
    onSearchBoxClicked: () -> Unit
) {
    val libraryItems = state.libraryItems

    LazyColumn {
        item {
            SearchBox(modifier = Modifier
                .padding(all = AppDimensions.spacing.medium)
                .clickable { onSearchBoxClicked() })
        }
        item {
            Text(
                text = stringResource(id = R.string.library),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(
                    start = AppDimensions.spacing.mediumLarge,
                    bottom = AppDimensions.spacing.medium
                )
            )
        }
        items(libraryItems) { item ->
            LibraryListItem(item = item) {
                onLibraryItemClicked(item)
            }
        }
    }
}
