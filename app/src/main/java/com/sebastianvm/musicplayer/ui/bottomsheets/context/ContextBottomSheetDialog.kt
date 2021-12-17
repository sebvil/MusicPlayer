package com.sebastianvm.musicplayer.ui.bottomsheets.context

import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.ui.components.lists.SingleLineListItem
import com.sebastianvm.musicplayer.ui.components.lists.SupportingImageType
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.util.compose.BottomSheetPreview
import kotlinx.coroutines.Dispatchers

@Composable
fun ContextBottomSheet(
    sheetViewModel: ContextMenuViewModel = viewModel(),
) {
    val state = sheetViewModel.state.collectAsState(context = Dispatchers.Main)
    ContextMenuLayout(state = state.value)
}

/**
 * The Android Studio Preview cannot handle this, but it can be run in device for preview
 */
@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ContextMenuScreenPreview(@PreviewParameter(ContextMenuStatePreviewParameterProvider::class) state: ContextMenuState) {
    BottomSheetPreview {
        ContextMenuLayout(state = state)
    }
}

@Composable
fun ContextMenuLayout(
    state: ContextMenuState
) {
    with(state) {
        LazyColumn {
            items(listItems, key = { it.text }) {
                SingleLineListItem(
                    supportingImage = { iconModifier ->
                        Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = DisplayableString.ResourceValue(it.text)
                                .getString(),
                            modifier = iconModifier,
                        )
                    },
                    supportingImageType = SupportingImageType.ICON
                ) {
                    Text(
                        text = stringResource(id = it.text),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.contextBottomSheet() {
    bottomSheet(
        route = createNavRoute(NavRoutes.CONTEXT, NavArgs.SCREEN),
        arguments = listOf(
            navArgument(NavArgs.SCREEN) { type = NavType.StringType }
        )
    ) {
        val sheetViewModel: ContextMenuViewModel = hiltViewModel()
        ContextBottomSheet(
            sheetViewModel = sheetViewModel
        )
    }
}
