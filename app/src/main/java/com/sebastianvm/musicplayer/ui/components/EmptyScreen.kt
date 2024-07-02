package com.sebastianvm.musicplayer.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.sync.LibrarySyncWorker
import com.sebastianvm.musicplayer.designsystem.components.Text

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
                dialogTitle = RString.storage_permission_needed,
                message = RString.grant_storage_permissions,
                onGrantPermission = {
                    val syncRequest = OneTimeWorkRequestBuilder<LibrarySyncWorker>().build()
                    WorkManager.getInstance(context).enqueue(syncRequest)
                },
            ) { onClick ->
                Button(onClick = onClick) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Text(text = stringResource(id = RString.refresh_library))
                }
            }
        },
        modifier = modifier,
    )
}
