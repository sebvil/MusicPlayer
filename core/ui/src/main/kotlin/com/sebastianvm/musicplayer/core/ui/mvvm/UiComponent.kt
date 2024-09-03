package com.sebastianvm.musicplayer.core.ui.mvvm

import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras

interface UiComponent {
    @Composable fun Content(modifier: Modifier)
}

abstract class MvvmComponent<S : State, UA : UserAction, VM : BaseViewModel<S, UA>> :
    ViewModelStoreOwner, UiComponent {

    protected abstract val viewModel: VM

    final override val viewModelStore: ViewModelStore = ViewModelStore()

    @Composable abstract fun Content(state: S, handle: Handler<UA>, modifier: Modifier)

    @Composable
    final override fun Content(modifier: Modifier) {
        val vm = remember { viewModel }
        val state by vm.currentState
        Content(state = state, handle = vm::handle, modifier = modifier)
    }

    fun clear() {
        viewModelStore.clear()
    }
}

val UiComponent.key: String
    get() = this.toString()

@MainThread
inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModels(
    noinline factory: (() -> VM)
): Lazy<VM> {
    val factoryPromise = {
        object : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory() as T
            }
        }
    }

    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise, { CreationExtras.Empty })
}
