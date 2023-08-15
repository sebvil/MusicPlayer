package com.sebastianvm.musicplayer.ui.components

import android.Manifest
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions

@OptIn(ExperimentalPermissionsApi::class)
data class PermissionHandlerState(
    val permissionState: PermissionState,
    val showPermissionDeniedDialog: Boolean,
    val permissionExplanationDialogState: PermissionDialogState,
    val permissionDeniedDialogState: PermissionDialogState
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permission: Permission,
    @StringRes dialogTitle: Int,
    @StringRes message: Int,
    onPermissionGranted: () -> Unit,
    content: @Composable (onClick: () -> Unit) -> Unit
) {
    var showPermissionDeniedDialog by remember {
        mutableStateOf(false)
    }
    val permissionState = if (LocalInspectionMode.current) {
        // Show this text in a preview window:
        object : PermissionState {
            override val permission: String
                get() = ""
            override val status: PermissionStatus
                get() = PermissionStatus.Granted

            override fun launchPermissionRequest() = Unit
        }
    } else {
        rememberPermissionState(
            permission = permission.permissionName,
            onPermissionResult = { isGranted ->
                showPermissionDeniedDialog = if (isGranted) {
                    onPermissionGranted()
                    false
                } else {
                    true
                }
            }
        )
    }

    val permissionHandlerState = remember {
        derivedStateOf {
            PermissionHandlerState(
                permissionState = permissionState,
                showPermissionDeniedDialog = showPermissionDeniedDialog,
                permissionDeniedDialogState = PermissionDialogState(
                    title = dialogTitle,
                    text = message,
                    confirmButtonText = R.string.ok
                ),
                permissionExplanationDialogState = PermissionDialogState(
                    title = dialogTitle,
                    text = message,
                    confirmButtonText = R.string.continue_string
                )
            )
        }
    }

    if (permissionHandlerState.value.showPermissionDeniedDialog && permissionHandlerState.value.permissionState.status.shouldShowRationale) {
        PermissionDialog(
            state = permissionHandlerState.value.permissionExplanationDialogState,
            onDismiss = { showPermissionDeniedDialog = false },
            onConfirm = {
                permissionHandlerState.value.permissionState.launchPermissionRequest()
            }
        )
    } else if (permissionHandlerState.value.showPermissionDeniedDialog) {
        PermissionDialog(
            state = permissionHandlerState.value.permissionDeniedDialogState,
            onDismiss = { showPermissionDeniedDialog = false },
            onConfirm = { showPermissionDeniedDialog = false }
        )
    }

    content {
        when (val status = permissionState.status) {
            is PermissionStatus.Granted -> {
                onPermissionGranted()
            }

            is PermissionStatus.Denied -> {
                if (status.shouldShowRationale) {
                    showPermissionDeniedDialog = true
                } else {
                    permissionState.launchPermissionRequest()
                }
            }
        }
    }
}

data class PermissionDialogState(
    @StringRes val title: Int,
    @StringRes val text: Int,
    @StringRes val confirmButtonText: Int
)

@Composable
fun PermissionDialog(
    state: PermissionDialogState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = state.title))
        },
        text = {
            Text(text = stringResource(id = state.text))
        },
        confirmButton = {
            Button(
                modifier = Modifier
                    .padding(horizontal = AppDimensions.spacing.xSmall),
                onClick = onConfirm
            ) {
                Text(text = stringResource(state.confirmButtonText))
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier
                    .padding(horizontal = AppDimensions.spacing.xSmall),
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        }
    )
}

sealed interface Permission {
    val permissionName: String

    data object ReadAudio : Permission {
        override val permissionName: String
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
    }
}
