package com.sebastianvm.musicplayer.ui.library.root

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.ui.components.PermissionDialogState
import com.sebastianvm.musicplayer.ui.components.PermissionHandler
import com.sebastianvm.musicplayer.ui.components.PermissionHandlerState
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


interface LibraryScreenActivityDelegate {
    fun navigateToLibraryScreen(route: String)
}

interface LibraryScreenDelegate : LibraryListDelegate

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LibraryScreen(
    screenViewModel: LibraryViewModel = viewModel(),
    delegate: LibraryScreenActivityDelegate
) {
    val context = LocalContext.current
    val showPermissionDeniedDialog = remember {
        mutableStateOf(false)
    }

    val storagePermissionState = rememberPermissionState(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                Intent(context, LibraryScanService::class.java).also { intent ->
                    startForegroundService(context, intent)
                }
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

    Screen(
        screenViewModel = screenViewModel,
        eventHandler = { event ->
            when (event) {
                is LibraryUiEvent.StartGetMusicService -> {
                    Intent(context, LibraryScanService::class.java).also { intent ->
                        startForegroundService(context, intent)
                    }
                }
                is LibraryUiEvent.NavigateToScreen -> {
                    delegate.navigateToLibraryScreen(event.rowId)
                }
                is LibraryUiEvent.RequestPermission -> {
                    storagePermissionState.launchPermissionRequest()
                }
            }
        },
        fab = {
            ScanFab(onClick = {
                when (storagePermissionState.status) {
                    is PermissionStatus.Granted -> {
                        Intent(context, LibraryScanService::class.java).also { intent ->
                            startForegroundService(context, intent)
                        }
                    }
                    is PermissionStatus.Denied -> {
                        if (storagePermissionState.status.shouldShowRationale) {
                            showPermissionDeniedDialog.value = true
                        } else {
                            storagePermissionState.launchPermissionRequest()
                        }
                    }
                }
            })
        }) { state ->
        LibraryLayout(
            state = state,
            object : LibraryScreenDelegate {
                override fun onRowClicked(rowId: String) {
                    screenViewModel.onRowClicked(rowId = rowId)
                }
            }
        )
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LibraryScreenPreview(@PreviewParameter(LibraryStateProvider::class) libraryState: LibraryState) {
    ScreenPreview {
        LibraryLayout(state = libraryState, delegate = object : LibraryScreenDelegate {
            override fun onRowClicked(rowId: String) = Unit
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryLayout(
    state: LibraryState,
    delegate: LibraryScreenDelegate
) {
    LibraryList(libraryItems = state.libraryItems, delegate = delegate)
}

@Preview
@Composable
fun ScanFab(onClick: () -> Unit = {}) {
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

interface LibraryListDelegate {
    fun onRowClicked(rowId: String)
}

@Composable
fun LibraryList(
    @PreviewParameter(LibraryItemListProvider::class) libraryItems: List<LibraryItem>,
    delegate: LibraryListDelegate
) {
    LazyColumn {
        item {
            Text(
                text = stringResource(id = R.string.library),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(
                    start = AppDimensions.spacing.mediumLarge,
                    top = AppDimensions.spacing.mediumLarge,
                    bottom = AppDimensions.spacing.medium
                )
            )
        }
        items(libraryItems) { item ->
            SingleLineListItem(
                modifier = Modifier.clickable {
                    delegate.onRowClicked(item.rowId)
                },
                supportingImage =
                { iconModifier ->
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.rowName),
                        modifier = iconModifier,
                    )
                },
                supportingImageType = SupportingImageType.AVATAR,
                afterListContent = {
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
            ) {
                Text(
                    text = stringResource(id = item.rowName),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
