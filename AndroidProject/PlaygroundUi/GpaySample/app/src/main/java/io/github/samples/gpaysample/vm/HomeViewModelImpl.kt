package io.github.samples.gpaysample.vm

import androidx.lifecycle.ViewModel
import io.github.samples.gpaysample.data.MockDataProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class HomeViewModelImpl() : ViewModel(), HomeViewModel {

    private val contentsState = MutableStateFlow(HomeViewModel.State(MockDataProvider.suica))

    override val state: StateFlow<HomeViewModel.State>
        get() = contentsState


    private val effectChannel = Channel<HomeViewModel.Effect>(Channel.UNLIMITED)
    override val effect: Flow<HomeViewModel.Effect> = effectChannel.receiveAsFlow()

    override fun event(event: HomeViewModel.Event) {
        when(event) {
            is HomeViewModel.Event.SuicaEvent -> this::handleSuicasEvent
        }
    }

    private fun handleSuicasEvent(event: HomeViewModel.Event.SuicaEvent) {
        when (event) {
            is HomeViewModel.Event.SuicaEvent.TapCell -> {

            }

            is HomeViewModel.Event.SuicaEvent.Charge -> {

            }
        }
    }

}