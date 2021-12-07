package com.sebastianvm.musicplayer.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.R
import org.junit.Rule
import org.junit.Test

class MediaArtImageKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMediaArtWithNoUrisUsesBackupResource() {
        val mediaArt = MediaArt(
            uris = listOf(),
            contentDescription = DisplayableString.StringValue(IMAGE_DESCRIPTION),
            backupResource = R.drawable.ic_album,
            backupContentDescription = DisplayableString.StringValue(BACKUP_DESCRIPTION)
        )

        composeTestRule.setContent {
           MediaArtImage(image = mediaArt)
        }

        composeTestRule.onNodeWithContentDescription(BACKUP_DESCRIPTION).assertIsDisplayed()

    }

    companion object {
        private const val IMAGE_DESCRIPTION = "IMAGE_DESCRIPTION"
        private const val BACKUP_DESCRIPTION = "BACKUP_DESCRIPTION"
    }
}