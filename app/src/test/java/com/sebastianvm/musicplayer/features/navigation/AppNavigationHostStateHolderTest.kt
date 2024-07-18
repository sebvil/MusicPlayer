package com.sebastianvm.musicplayer.features.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianvm.musicplayer.core.ui.mvvm.NoArguments
import com.sebastianvm.musicplayer.core.ui.mvvm.NoState
import com.sebastianvm.musicplayer.core.ui.mvvm.NoUserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.home.HomeUiComponent
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class AppNavigationHostStateHolderTest :
    FreeSpec({
        fun TestScope.getSubject(): AppNavigationHostStateHolder {
            return AppNavigationHostStateHolder(stateHolderScope = this)
        }

        "init sets initial screen to HomeUiComponent" {
            val subject = getSubject()
            testStateHolderState(subject) {
                val state = awaitItem()
                state.backStack shouldHaveSize 1
                val entry = state.backStack.last()
                entry.uiComponent.shouldBeInstanceOf<HomeUiComponent>()
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
                                            AppNavigationAction.ShowScreen(
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
                                            AppNavigationAction.ShowScreen(
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
                        subject.handle(AppNavigationAction.PopBackStack)
                        val state = awaitItem()
                        state.backStack.shouldBeEmpty()
                    }
                }
            }
    }) {

    object FakeUiComponent : UiComponent<NoArguments, StateHolder<NoState, NoUserAction>> {
        override val arguments: NoArguments = NoArguments

        override val key: Any = this

        override fun createStateHolder(
            dependencies: Dependencies
        ): StateHolder<NoState, NoUserAction> {
            error("Should not need to create state holder for tests")
        }

        @Composable override fun Content(modifier: Modifier) = Unit
    }
}
