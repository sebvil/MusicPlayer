package com.sebastianvm.musicplayer.ui.components

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.LibraryScanService

@Composable
fun EmptyScreen(
    message: @Composable () -> Unit,
    button: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(space = 16.dp, alignment = Alignment.CenterVertically),
    ) {
        message()
        button()
    }
}

@Composable
fun StoragePermissionNeededEmptyScreen(@StringRes message: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    EmptyScreen(
        message = { Text(text = stringResource(id = message), textAlign = TextAlign.Center) },
        button = {
            PermissionHandler(
                permission = Permission.ReadAudio,
                dialogTitle = R.string.storage_permission_needed,
                message = R.string.grant_storage_permissions,
                onPermissionGranted = {
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, LibraryScanService::class.java),
                    )
                },
            ) { onClick ->
                Button(onClick = onClick) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Text(text = stringResource(id = R.string.refresh_library))
                }
            }
        },
        modifier = modifier,
    )
}
