package com.sebastianvm.musicplayer.core.designsystems.components

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.sebastianvm.musicplayer.core.resources.RString
import kotlinx.parcelize.Parcelize

object TextFieldDialog {

    @Parcelize
    data class State(
        @StringRes val title: Int,
        @StringRes val confirmButtonText: Int,
        val initialText: String = "",
        @StringRes val errorMessage: Int? = null,
        val onDismiss: () -> Unit,
        val onSave: (text: String) -> Unit,
    ) : StateUiComponent {
        @Composable
        override fun Content(modifier: Modifier) {
            TextFieldDialog(
                title = title,
                confirmButtonText = confirmButtonText,
                initialText = initialText,
                onDismiss = onDismiss,
                onSave = onSave,
                modifier = modifier,
                errorMessage = errorMessage,
            )
        }
    }
}

@Composable
fun TextFieldDialog(
    @StringRes title: Int,
    @StringRes confirmButtonText: Int,
    initialText: String,
    onDismiss: () -> Unit,
    onSave: (text: String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: Int? = null,
) {
    var text by remember {
        mutableStateOf(TextFieldValue(initialText, selection = TextRange(initialText.length)))
    }
    val focusRequester = remember { FocusRequester() }

    var canShowError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    canShowError = true
                    onSave(text.text)
                }
            ) {
                Text(text = stringResource(confirmButtonText))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) { Text(text = stringResource(RString.cancel)) }
        },
        modifier = modifier,
        title = { Text(text = stringResource(title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { newValue ->
                    canShowError = false
                    text = newValue
                },
                modifier =
                    Modifier.focusRequester(focusRequester).onPlaced {
                        focusRequester.requestFocus()
                    },
                supportingText =
                    if (canShowError) {
                        errorMessage?.let { { Text(text = stringResource(it)) } }
                    } else null,
                isError = canShowError && errorMessage != null,
            )
        },
    )
}
