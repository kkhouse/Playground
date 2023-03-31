package io.github.samples.gpaysample.vm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import io.github.samples.gpaysample.data.Suica
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface HomeViewModel :
    UnidirectionalViewModel<HomeViewModel.Event, HomeViewModel.Effect, HomeViewModel.State>{
    data class State(
        val content: Suica
    )
    sealed class Effect {
        data class Mock(val mock: String)
    }
    sealed class Event {
        sealed class SuicaEvent : Event() {
            object TapCell: SuicaEvent()
            object Charge : SuicaEvent()
        }
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

private val LocalHomeViewModelFactory = compositionLocalOf<@Composable () -> HomeViewModel> {
    {
        error("not LocalFeedViewModelFactory provided")
    }
}

fun provideHomeViewModelFactory(viewModelFactory: @Composable () -> HomeViewModel) =
    LocalHomeViewModelFactory provides viewModelFactory

@Composable
fun homeViewModel() = LocalHomeViewModelFactory.current()