package com.sebastianvm.musicplayer.features.test.sort

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature

class FakeSortMenuFeature : SortMenuFeature {
    override fun sortMenuUiComponent(arguments: SortMenuArguments): UiComponent<*> {
        return FakeUiComponent(arguments, "SortMenu")
    }
}
