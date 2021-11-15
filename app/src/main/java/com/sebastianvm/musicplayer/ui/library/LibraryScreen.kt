package com.sebastianvm.musicplayer.ui.library

import android.Manifest
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.ResUtil
import com.sebastianvm.musicplayer.*
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.components.*
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview

interface LibraryScreenActivityDelegate {
    @PermissionStatus
    fun getPermissionStatus(permission: String): String
    fun openAppSettings()
    fun navigateToLibraryScreen(route: String)
}

interface LibraryScreenDelegate : LibraryListDelegate, PermissionDeniedDialogDelegate,
    RequestDialogDelegate {
    fun onFabClicked()
    fun onDismissProgressDialog()
}


@Composable
fun LibraryScreen(
    screenViewModel: LibraryViewModel = viewModel(),
    delegate: LibraryScreenActivityDelegate
) {
    val state = screenViewModel.state.observeAsState(screenViewModel.state.value)
    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                screenViewModel.handle(LibraryUserAction.GetMusic)
            } else {
                when (delegate.getPermissionStatus(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    SHOULD_SHOW_EXPLANATION -> screenViewModel.handle(LibraryUserAction.ShowPermissionExplanationDialog)
                    else -> screenViewModel.handle(LibraryUserAction.ShowPermissionDeniedDialog)
                }
            }
        }
    )


    // TODO create service for loading large library
    LibraryLayout(
        state = state.value,
        object : LibraryScreenDelegate {
            override fun onFabClicked() {
                when (delegate.getPermissionStatus(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    PERMISSION_GRANTED -> screenViewModel.handle(LibraryUserAction.GetMusic)
                    SHOULD_SHOW_EXPLANATION -> screenViewModel.handle(LibraryUserAction.ShowPermissionExplanationDialog)
                    SHOULD_REQUEST_PERMISSION -> requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

            override fun onRowClicked(rowGid: String) {
                delegate.navigateToLibraryScreen(route = rowGid)
            }

            override fun onPermissionDeniedDialogDismissRequest() {
                screenViewModel.handle(LibraryUserAction.DismissPermissionDeniedDialog)
            }

            override fun onPermissionDeniedConfirmButtonClicked() {
                delegate.openAppSettings()
            }

            override fun onRequestDialogDismissRequest() {
                screenViewModel.handle(LibraryUserAction.DismissPermissionExplanationDialog)
            }

            override fun onDismissProgressDialog() {
                screenViewModel.handle(LibraryUserAction.DismissProgressDialog)
            }

            override fun onContinueClicked() {
                screenViewModel.handle(LibraryUserAction.DismissPermissionExplanationDialog)
                requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }
    )
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
                    .padding(horizontal = AppDimensions.spacing.small),
                onClick = delegate::onContinueClicked
            ) {
                Text(text = stringResource(R.string.continue_string))
            }
        },
        dismissButton = {
            Button(
                modifier = Modifier
                    .padding(horizontal = AppDimensions.spacing.small),
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
                modifier = Modifier.padding(horizontal = AppDimensions.spacing.small),
                onClick = delegate::onPermissionDeniedDialogDismissRequest
            ) {
                Text(text = stringResource(R.string.dismiss))
            }
        },
        confirmButton = {
            Button(
                modifier = Modifier.padding(horizontal = AppDimensions.spacing.small),
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
            override fun onFabClicked() = Unit

            override fun onDismissProgressDialog() = Unit

            override fun onRowClicked(rowGid: String) = Unit

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
    if (state.isLoading) {
        ProgressDialog { delegate.onDismissProgressDialog() }
    }

    if (state.showPermissionDeniedDialog) {
        PermissionDeniedDialog(delegate = delegate)
    }

    if (state.showPermissionExplanationDialog) {
        RequestDialog(delegate = delegate)
    }

    Scaffold(floatingActionButton = {
        ScanFab(onClick = delegate::onFabClicked)
    }) {
        LibraryList(libraryItems = state.libraryItems, delegate = delegate)
    }

}

@Preview
@Composable
fun ScanFab(onClick: () -> Unit = {}) {
    ExtendedFloatingActionButton(
        text = {
            Text(
                text = stringResource(id = R.string.scan),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = AppDimensions.spacing.mediumLarge),
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
    fun onRowClicked(rowGid: String)
}

@Composable
fun LibraryList(
    @PreviewParameter(LibraryItemListProvider::class) libraryItems: List<LibraryItem>,
    delegate: LibraryListDelegate,
) {
    val listState = ListWithHeaderState(
        DisplayableString.ResourceValue(R.string.library),
        libraryItems,
        { header -> LibraryTitle(title = header) },
        { item ->
            LibraryRow(item, modifier = Modifier
                .fillMaxWidth()
                .clickable { delegate.onRowClicked(item.rowId) }
                .padding(horizontal = 32.dp, vertical = AppDimensions.spacing.mediumLarge))
        }
    )
    ListWithHeader(state = listState)

}


@Composable
fun LibraryRow(
    @PreviewParameter(LibraryItemProvider::class) libraryItem: LibraryItem,
    modifier: Modifier = Modifier,
) {
    val sectionName = DisplayableString.ResourceValue(libraryItem.rowName)
    val textWithIconState =
        TextWithIconState(
            icon = libraryItem.icon,
            iconContentDescription = DisplayableString.ResourceValue(value = libraryItem.rowName),
            text = sectionName
        )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextWithIcon(
            state = textWithIconState,
        )
        Text(
            text = ResUtil.getQuantityString(
                LocalContext.current,
                libraryItem.countString,
                libraryItem.count.toInt(),
                libraryItem.count
            ), style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


