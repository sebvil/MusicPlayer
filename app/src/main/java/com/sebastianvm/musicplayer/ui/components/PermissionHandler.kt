package com.sebastianvm.musicplayer.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions


data class PermissionHandlerState @OptIn(ExperimentalPermissionsApi::class) constructor(
    val permissionState: PermissionState,
    val showPermissionDeniedDialog: Boolean,
    val permissionExplanationDialogState: PermissionDialogState,
    val permissionDeniedDialogState: PermissionDialogState
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    state: PermissionHandlerState,
    onPermissionDeniedDialogDismissed: () -> Unit,
) {

    val context = LocalContext.current

    val navigateToSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            state.permissionState.launchPermissionRequest()
        }
    )
    if (state.showPermissionDeniedDialog && state.permissionState.status.shouldShowRationale) {
        PermissionDialog(
            state = state.permissionExplanationDialogState,
            onDismiss = onPermissionDeniedDialogDismissed,
            onConfirm = {
                state.permissionState.launchPermissionRequest()
            })
    } else if (state.showPermissionDeniedDialog) {
        PermissionDialog(
            state = state.permissionDeniedDialogState,
            onDismiss = onPermissionDeniedDialogDismissed,
            onConfirm = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                )
                intent.data = Uri.parse("package:${context.packageName}")
                if (intent.resolveActivity(context.packageManager) != null) {
                    navigateToSettingsLauncher.launch(intent)
                }
            })
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