package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.test.initializeFakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class NavigationHostViewModelTest :
    FreeSpec({
        fun TestScope.getSubject(): NavigationHostViewModel {
            return NavigationHostViewModel(vmScope = this, features = initializeFakeFeatures())
        }

        "init sets initial screen to HomeUiComponent" {
            val subject = getSubject()
            testViewModelState(subject) {
                val state = awaitItem()
                state.backStack shouldHaveSize 1
                val entry = state.backStack.last()

                entry.mvvmComponent shouldBe FakeMvvmComponent(name = "Home", arguments = null)
                entry.presentationMode shouldBe NavOptions.PresentationMode.Screen
            }
        }

        "handle" -
            {
                "ShowScreen adds screen to backstack" -
                    {
                        "when location is kept in backstack" -
                            {
                                withData(
                                    nameFn = { "when screen is ${it.name}" },
                                    NavOptions.PresentationMode.entries,
                                ) { presentationMode ->
                                    val subject = getSubject()
                                    testViewModelState(subject) {
                                        awaitItem()
                                        subject.handle(
                                            NavigationAction.ShowScreen(
                                                mvvmComponent = FakeMvvmComponent,
                                                navOptions =
                                                    NavOptions(
                                                        popCurrent = false,
                                                        presentationMode = presentationMode,
                                                    ),
                                            )
                                        )
                                        val state = awaitItem()
                                        state.backStack shouldHaveSize 2
                                        val entry = state.backStack.last()
                                        entry.mvvmComponent shouldBe FakeMvvmComponent
                                        entry.presentationMode shouldBe presentationMode
                                    }
                                }
                            }

                        "when location is not kept in backstack" -
                            {
                                withData(
                                    nameFn = { "when screen is ${it.name}" },
                                    NavOptions.PresentationMode.entries,
                                ) { presentationMode ->
                                    val subject = getSubject()
                                    testViewModelState(subject) {
                                        awaitItem()
                                        subject.handle(
                                            NavigationAction.ShowScreen(
                                                mvvmComponent = FakeMvvmComponent,
                                                navOptions =
                                                    NavOptions(
                                                        popCurrent = true,
                                                        presentationMode = presentationMode,
                                                    ),
                                            )
                                        )
                                        val state = awaitItem()
                                        state.backStack shouldHaveSize 1
                                        val entry = state.backStack.last()
                                        entry.mvvmComponent shouldBe FakeMvvmComponent
                                        entry.presentationMode shouldBe presentationMode
                                    }
                                }
                            }
                    }

                "PopBackStack removes screen from backstack" {
                    val subject = getSubject()
                    testViewModelState(subject) {
                        awaitItem()
                        subject.handle(NavigationAction.PopBackStack)
                        val state = awaitItem()
                        state.backStack.shouldBeEmpty()
                    }
                }
            }
    }) {

    private object FakeMvvmComponent : MvvmComponent {

        @Composable override fun Content(modifier: Modifier) = Unit
    }
}
