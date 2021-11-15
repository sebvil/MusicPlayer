package com.sebastianvm.musicplayer.ui.components

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview


@Preview
@Composable
fun ProgressDialogPreview() {
    ThemedPreview {
        ProgressDialog()
    }
}


@Composable
fun ProgressDialog(onDismissRequest: () -> Unit = {}) {
    Dialog(onDismissRequest = onDismissRequest) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

