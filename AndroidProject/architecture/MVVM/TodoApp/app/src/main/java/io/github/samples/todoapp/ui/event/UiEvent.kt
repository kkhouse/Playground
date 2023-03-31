package io.github.samples.todoapp.ui.event

sealed class UiEvent {
    object PopBackStack: UiEvent()
    data class showSnackBar(
        val message: String,
        val action: String? = null
    ): UiEvent()
    data class onNavigate(val route: String): UiEvent()
}