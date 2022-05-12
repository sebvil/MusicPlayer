package com.sebastianvm.musicplayer.ui.library.root

import android.Manifest
import android.content.Intent
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.sebastianvm.musicplayer.ui.util.compose.ComposePreviews
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


interface LibraryScreenNavigationDelegate {
    fun navigateToLibraryScreen(route: String)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LibraryScreen(
    screenViewModel: LibraryViewModel = viewModel(),
    delegate: LibraryScreenNavigationDelegate
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
            ExtendedFloatingActionButton(
                text = {
                    Text(
                        text = stringResource(id = R.string.scan),
                    )
                },
                onClick = {
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
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scan),
                        contentDescription = stringResource(id = R.string.scan)
                    )
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

@Composable
@ComposePreviews
fun LibraryScreenPreview(@PreviewParameter(LibraryStateProvider::class) libraryState: LibraryState) {
    ScreenPreview {
        LibraryLayout(state = libraryState, delegate = object : LibraryScreenDelegate {
            override fun onRowClicked(rowId: String) = Unit
        })
    }
}

