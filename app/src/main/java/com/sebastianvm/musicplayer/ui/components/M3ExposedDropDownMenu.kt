package com.sebastianvm.musicplayer.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sebastianvm.musicplayer.ui.theme.outlinedTextFieldColors
import com.sebastianvm.musicplayer.ui.util.compose.AppDimensions
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


data class M3ExposedDropDownMenuState(
    val expanded: Boolean,
    val label: String,
    val options: List<String>,
    val chosenOption: String
)

interface M3ExposedDropDownMenuDelegate {
    fun toggleExpanded() = Unit
    fun optionChosen(newOption: String) = Unit
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun M3ExposedDropDownMenu(
    state: M3ExposedDropDownMenuState,
    delegate: M3ExposedDropDownMenuDelegate,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = state.expanded,
        onExpandedChange = { delegate.toggleExpanded() },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = state.chosenOption,
            onValueChange = { },
            label = { Text(state.label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = state.expanded
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = state.expanded,
            onDismissRequest = { delegate.toggleExpanded() },
        ) {
            state.options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        delegate.optionChosen(selectionOption)
                    },
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun ChooseDropdownMenuPreview() {

    ThemedPreview {
        val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf(options[0]) }
        M3ExposedDropDownMenu(
            state = M3ExposedDropDownMenuState(
                expanded = expanded,
                label = "Options",
                options = options,
                chosenOption = selectedOptionText
            ),
            delegate = object : M3ExposedDropDownMenuDelegate {
                override fun toggleExpanded() {
                    expanded = !expanded
                }

                override fun optionChosen(newOption: String) {
                    selectedOptionText = newOption
                    expanded = false
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = AppDimensions.spacing.medium)
        )
    }
}
