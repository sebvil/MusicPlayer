package com.sebastianvm.musicplayer.ui.library.root

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.PermissionStatus
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.LibraryScanService
import com.sebastianvm.musicplayer.ui.components.LibraryTitle
import com.sebastianvm.musicplayer.ui.components.ListWithHeader
import com.sebastianvm.musicplayer.ui.components.ListWithHeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ListItemDelegate
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview


// TODO investigate scan not starting after permission granted
interface LibraryScreenActivityDelegate {
    @PermissionStatus
    fun getPermissionStatus(permission: String): String
    fun navigateToLibraryScreen(route: String)
}

interface LibraryScreenDelegate : LibraryListDelegate, PermissionDeniedDialogDelegate,
    RequestDialogDelegate

@Composable
fun LibraryScreen(
    screenViewModel: LibraryViewModel = viewModel(),
    delegate: LibraryScreenActivityDelegate
) {
    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                screenViewModel.handle(LibraryUserAction.PermissionGranted)
            } else {
                screenViewModel.handle(
                    LibraryUserAction.PermissionDenied(
                        delegate.getPermissionStatus(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                )
            }
        }
    )
    val navigateToSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (delegate.getPermissionStatus(Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                screenViewModel.handle(LibraryUserAction.PermissionGranted)
                screenViewModel.handle(LibraryUserAction.DismissPermissionDeniedDialog)
            }
        }
    )

    val context = LocalContext.current
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
                    requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                is LibraryUiEvent.OpenAppSettings -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:${context.packageName}")
                    if (intent.resolveActivity(context.packageManager) != null) {
                        navigateToSettingsLauncher.launch(intent)
                    }
                }
            }
        },
        fab = {
            ScanFab(onClick = {
                screenViewModel.handle(
                    LibraryUserAction.FabClicked(
                        delegate.getPermissionStatus(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                )
            })

        }) { state ->
        LibraryLayout(
            state = state,
            object : LibraryScreenDelegate {
                override fun onRowClicked(rowId: String) {
                    screenViewModel.handle(LibraryUserAction.RowClicked(rowId = rowId))
                }

                override fun onPermissionDeniedDialogDismissRequest() {
                    screenViewModel.handle(LibraryUserAction.DismissPermissionDeniedDialog)
                }

                override fun onPermissionDeniedConfirmButtonClicked() {
                    screenViewModel.handle(LibraryUserAction.PermissionDeniedConfirmButtonClicked)
                }

                override fun onRequestDialogDismissRequest() {
                    screenViewModel.handle(LibraryUserAction.DismissPermissionExplanationDialog)
                }


                override fun onContinueClicked() {
                    screenViewModel.handle(LibraryUserAction.PermissionExplanationDialogContinueClicked)
                }
            }
        )
    }
}

interface RequestDialogDelegate {
    fun onRequestDialogDismissRequest()
    fun onContinueClicked()
}

@Composable
fun RequestDialog(delegate: RequestDialogDelegate) {
    AlertDialog(
        onDismissRequest = delegate::onRequestDialogDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.storage_permission_needed))
        },
        text = {
            Text(text = stringResource(id = R.string.grant_storage_permissions))
        },
        confirmButton = {
            Button(
                modifier = Modifier
                    .padding(horizontal = AppDimensions.spacing.xSmall),
                onClick = delegate::onContinueClicked
            ) {
                Text(text = stringResource(R.string.continue_string))
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier
                    .padding(horizontal = AppDimensions.spacing.xSmall),
                onClick = delegate::onRequestDialogDismissRequest
            ) {
                Text(text = stringResource(id = R.string.dismiss))
            }
        }
    )
}

interface PermissionDeniedDialogDelegate {
    fun onPermissionDeniedDialogDismissRequest()
    fun onPermissionDeniedConfirmButtonClicked()
}

@Composable
fun PermissionDeniedDialog(delegate: PermissionDeniedDialogDelegate) {
    AlertDialog(
        onDismissRequest = delegate::onPermissionDeniedDialogDismissRequest,
        title = {
            Text(text = stringResource(id = R.string.storage_permission_needed))
        },
        text = {
            Text(text = stringResource(id = R.string.grant_storage_permissions))
        },
        dismissButton = {
            Button(
                modifier = Modifier.padding(horizontal = AppDimensions.spacing.xSmall),
                onClick = delegate::onPermissionDeniedDialogDismissRequest
            ) {
                Text(text = stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.padding(horizontal = AppDimensions.spacing.xSmall),
                onClick = delegate::onPermissionDeniedConfirmButtonClicked
            ) {
                Text(text = stringResource(id = R.string.go_to_settings))
            }
        }
    )
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LibraryScreenPreview(@PreviewParameter(LibraryStateProvider::class) libraryState: LibraryState) {
    ScreenPreview {
        LibraryLayout(state = libraryState, delegate = object : LibraryScreenDelegate {
            override fun onRowClicked(rowId: String) = Unit

            override fun onPermissionDeniedDialogDismissRequest() = Unit

            override fun onPermissionDeniedConfirmButtonClicked() = Unit

            override fun onRequestDialogDismissRequest() = Unit

            override fun onContinueClicked() = Unit

        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryLayout(
    state: LibraryState,
    delegate: LibraryScreenDelegate
) {
    if (state.showPermissionDeniedDialog) {
        PermissionDeniedDialog(delegate = delegate)
    }

    if (state.showPermissionExplanationDialog) {
        RequestDialog(delegate = delegate)
    }
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
    val listState = ListWithHeaderState(
        DisplayableString.ResourceValue(R.string.library),
        libraryItems,
        { header -> LibraryTitle(title = header) },
        { item ->
            SingleLineListItem(
                supportingImage =
                { iconModifier ->
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = DisplayableString.ResourceValue(item.rowName)
                            .getString(),
                        modifier = iconModifier,
                    )
                },
                supportingImageType = SupportingImageType.AVATAR,
                afterListContent = {
                    Text(
                        text = ResUtil.getQuantityString(LocalContext.current, item.countString, item.count.toInt(), item.count),
                        modifier = Modifier.padding(horizontal = AppDimensions.spacing.medium),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                delegate = object : ListItemDelegate {
                    override fun onItemClicked() {
                        delegate.onRowClicked(item.rowId)
                    }
                }
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
    )
    ListWithHeader(state = listState)

}


