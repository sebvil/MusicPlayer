package com.sebastianvm.musicplayer.features.navigation

import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.test.TestScope

class AppNavigationHostStateHolderTest :
    FreeSpec({
        fun TestScope.getSubject(): AppNavigationHostStateHolder {
            return AppNavigationHostStateHolder(stateHolderScope = this)
        }
    })
