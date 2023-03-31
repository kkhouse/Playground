package io.github.samples.financedarkthemeany

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import io.github.samples.financedarkthemeany.other.AppError
import io.github.samples.financedarkthemeany.other.ContentState
import io.github.samples.financedarkthemeany.unidirectinalviewmodel.UnidirectionalViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface MainViewModel :
    UnidirectionalViewModel<MainViewModel.Event, MainViewModel.Effect, MainViewModel.State> {
    data class State(
        val showProgress: Boolean = false,
        val mainContentState: ContentState = ContentState("sample")
    )

    sealed class Effect {
        data class ErrorMessage(val appError: AppError) : Effect()
    }

    sealed class Event {
        object OnClickIcon: Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

private val LocalFeedViewModelFactory = compositionLocalOf<@Composable () -> MainViewModel> {
    {
        error("not LocalFeedViewModelFactory provided")
    }
}

fun provideFeedViewModelFactory(viewModelFactory: @Composable () -> MainViewModel) =
    LocalFeedViewModelFactory provides viewModelFactory

@Composable
fun mainViewModel() = LocalFeedViewModelFactory.current()


class MainViewModelImpl :MainViewModel  {
    override val state: StateFlow<MainViewModel.State>
        get() = MutableStateFlow(MainViewModel.State(false))
    override val effect: Flow<MainViewModel.Effect>
        get() = MutableStateFlow(MainViewModel.Effect.ErrorMessage(AppError()))

    override fun event(event: MainViewModel.Event) {

    }

}


