package com.sebastianvm.musicplayer.ui.library.root

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startForegroundService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.ui.components.PermissionDialogState
import com.sebastianvm.musicplayer.ui.components.PermissionHandler
import com.sebastianvm.musicplayer.ui.components.PermissionHandlerState
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ScreenLayout
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultViewModelInterfaceProvider
import com.sebastianvm.musicplayer.ui.util.mvvm.ScreenDelegate


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LibraryScreen(
    state: LibraryState,
    screenDelegate: ScreenDelegate<LibraryUserAction>
) {
    val context = LocalContext.current
    val showPermissionDeniedDialog = remember {
        mutableStateOf(false)
    }

    val storagePermissionState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                startForegroundService(
                    context,
                    Intent(context, LibraryScanService::class.java)
                )
                showPermissionDeniedDialog.value = false
            } else {
                showPermissionDeniedDialog.value = true
            }
        })

    val permissionHandlerState = remember {
        derivedStateOf {
            PermissionHandlerState(
                permissionState = storagePermissionState,
                showPermissionDeniedDialog = showPermissionDeniedDialog.value,
                permissionDeniedDialogState = PermissionDialogState(
                    title = R.string.storage_permission_needed,
                    text = R.string.grant_storage_permissions,
                    confirmButtonText = R.string.go_to_settings
                ),
                permissionExplanationDialogState = PermissionDialogState(
                    title = R.string.storage_permission_needed,
                    text = R.string.grant_storage_permissions,
                    confirmButtonText = R.string.continue_string
                )
            )
        }
    }

    PermissionHandler(
        state = permissionHandlerState.value,
        onPermissionDeniedDialogDismissed = {
            showPermissionDeniedDialog.value = false
        })

    LibraryScreenLayout(state = state, screenDelegate = screenDelegate, onFabClicked = {
        when (val status = storagePermissionState.status) {
            is PermissionStatus.Granted -> {
                startForegroundService(
                    context,
                    Intent(context, LibraryScanService::class.java)
                )
            }

            is PermissionStatus.Denied -> {
                if (status.shouldShowRationale) {
                    showPermissionDeniedDialog.value = true
                } else {
                    storagePermissionState.launchPermissionRequest()
                }
            }
        }
    })
}

@Composable
fun LibraryScreenLayout(
    state: LibraryState,
    screenDelegate: ScreenDelegate<LibraryUserAction> = DefaultViewModelInterfaceProvider.getDefaultInstance(),
    onFabClicked: () -> Unit
) {
    ScreenLayout(fab = {
        ExtendedFloatingActionButton(
            text = {
                Text(
                    text = stringResource(id = R.string.scan),
                )
            },
            onClick = onFabClicked,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_scan),
                    contentDescription = stringResource(id = R.string.scan)
                )
            })
    }) {
        LibraryLayout(state = state, screenDelegate = screenDelegate)
    }
}


@ComponentPreview
@Composable
fun SearchBox(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(AppDimensions.spacing.small)
            )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = stringResource(id = R.string.search),
            modifier = Modifier.padding(all = AppDimensions.spacing.small),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibraryLayout(state: LibraryState, screenDelegate: ScreenDelegate<LibraryUserAction>) {
    val libraryItems = state.libraryItems

    LazyColumn {
        item {
            SearchBox(modifier = Modifier
                .padding(all = AppDimensions.spacing.medium)
                .clickable {
                    screenDelegate.handle(LibraryUserAction.SearchBoxClicked)
                })
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
            ListItem(
                headlineText = {
                    Text(
                        text = stringResource(id = item.rowName),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },

                modifier = Modifier.clickable {
                    screenDelegate.handle(LibraryUserAction.RowClicked(item.destination))
                },
                leadingContent =
                {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.rowName),
                        modifier = Modifier.size(40.dp),
                    )
                },
                trailingContent = {
                    Text(
                        text = pluralStringResource(
                            id = item.countString,
                            count = item.count,
                            item.count
                        ),
                        modifier = Modifier.padding(horizontal = AppDimensions.spacing.medium),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}
