package com.sebastianvm.musicplayer.ui.components.searchfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.sebastianvm.musicplayer.designsystem.components.Text
import com.sebastianvm.musicplayer.util.resources.RString

@Composable
fun SearchField(
    onTextChanged: (newText: String) -> Unit,
    onUpButtonClicked: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val input = rememberSaveable { mutableStateOf("") }

    TextField(
        value = input.value,
        onValueChange = {
            input.value = it
            onTextChanged(it)
        },
        placeholder = {
            Text(
                text = stringResource(RString.search),
                style = LocalTextStyle.current,
                color = LocalContentColor.current,
            )
        },
        leadingIcon = {
            IconButton(onClick = { onUpButtonClicked() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = RString.search),
                )
            }
        },
        trailingIcon =
            input.value
                .takeUnless { it.isEmpty() }
                ?.let {
                    {
                        IconButton(
                            onClick = {
                                input.value = ""
                                onTextChanged("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = RString.search),
                            )
                        }
                    }
                },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onAny = { keyboardController?.hide() }),
        modifier = modifier.fillMaxWidth().focusRequester(focusRequester),
    )
}
