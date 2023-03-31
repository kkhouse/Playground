package io.github.samples.todoapp.ui.todo_list

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.samples.todoapp.ui.event.UiEvent

@Composable
fun Other(
    onNavigate: (UiEvent.PopBackStack) -> Unit
) {
    Text("other", modifier = Modifier.clickable {
        onNavigate(UiEvent.PopBackStack)
    })
}