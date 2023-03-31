package io.github.samples.hiltsample

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val hoge: HogeInterface
) : ViewModel() {

    private var _tapText = MutableStateFlow<String>("Text")
    val tapText = _tapText

    fun onEvent(event: CardEvent) {
        when(event) {
            is CardEvent.Tap -> {
                _tapText.value = hoge.hoge()
            }
        }
    }
}