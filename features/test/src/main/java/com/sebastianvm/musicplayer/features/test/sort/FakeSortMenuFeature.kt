package com.sebastianvm.musicplayer.features.test.sort

import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature

class FakeSortMenuFeature : SortMenuFeature {
    override fun sortMenuUiComponent(arguments: SortMenuArguments): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments, "SortMenu")
    }
}
