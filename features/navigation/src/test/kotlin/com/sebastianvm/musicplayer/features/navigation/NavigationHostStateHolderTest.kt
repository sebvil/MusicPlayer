package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.ui.mvvm.NoState
import com.sebastianvm.musicplayer.core.ui.mvvm.NoUserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.test.initializeFakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class NavigationHostStateHolderTest :
    FreeSpec({
        fun TestScope.getSubject(): NavigationHostStateHolder {
            return NavigationHostStateHolder(
                stateHolderScope = this,
                features = initializeFakeFeatures(),
            )
        }

        "init sets initial screen to HomeUiComponent" {
            val subject = getSubject()
            testStateHolderState(subject) {
                val state = awaitItem()
                state.backStack shouldHaveSize 1
                val entry = state.backStack.last()

                entry.uiComponent shouldBe FakeUiComponent(name = "Home", arguments = null)
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
                                    testStateHolderState(subject) {
                                        awaitItem()
                                        subject.handle(
                                            NavigationAction.ShowScreen(
                                                uiComponent = FakeUiComponent,
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
                                        entry.uiComponent shouldBe FakeUiComponent
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
                                    testStateHolderState(subject) {
                                        awaitItem()
                                        subject.handle(
                                            NavigationAction.ShowScreen(
                                                uiComponent = FakeUiComponent,
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
                                        entry.uiComponent shouldBe FakeUiComponent
                                        entry.presentationMode shouldBe presentationMode
                                    }
                                }
                            }
                    }

                "PopBackStack removes screen from backstack" {
                    val subject = getSubject()
                    testStateHolderState(subject) {
                        awaitItem()
                        subject.handle(NavigationAction.PopBackStack)
                        val state = awaitItem()
                        state.backStack.shouldBeEmpty()
                    }
                }
            }
    }) {

    private object FakeUiComponent : UiComponent<StateHolder<NoState, NoUserAction>> {

        override fun createStateHolder(services: Services): StateHolder<NoState, NoUserAction> {
            error("Should not need to create state holder for tests")
        }

        @Composable override fun Content(modifier: Modifier) = Unit
    }
}
