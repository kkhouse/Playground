package io.github.samples.todoapp.ui.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.samples.todoapp.domain.model.Todo
import io.github.samples.todoapp.domain.repository.TodoRepository
import io.github.samples.todoapp.ui.event.TodoListEvent
import io.github.samples.todoapp.ui.event.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.github.samples.todoapp.ui.navigation.Route

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {
    var todoList = repository.getTodoList()

    private val _UiEvent = Channel<UiEvent>() // Channelを経由しないと、EventのCollectができない??
    val uiEvent = _UiEvent.receiveAsFlow()

    private var deletedTodo: Todo? = null

    fun onEvent(event: TodoListEvent) {
        when(event) {
            is TodoListEvent.onDeleteTodo -> {
                deletedTodo = event.todo
                viewModelScope.launch {
                    repository.deleteTodo(event.todo)
                }
                sendUiEvent(UiEvent.showSnackBar(
                    message = "deleted",
                    action = "Undo"
                ))
            }
            is TodoListEvent.onDoneTodo -> {

            }
            is TodoListEvent.onAddTodo -> {
                viewModelScope.launch {
                    repository.addTodo(TodoGenerator()[(0..4).random()])
                }
            }
            is TodoListEvent.onClickTodo -> {
                sendUiEvent(UiEvent.onNavigate(Route.Other.routePath))
            }
            is TodoListEvent.onUndoTodoDelete -> {
                viewModelScope.launch {
                    deletedTodo?.let {
                        repository.addTodo(it)
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _UiEvent.send(event)
        }
    }

    private fun TodoGenerator(): List<Todo> {
        return listOf(
            Todo("title", Math.random().toString(), false, 1),
            Todo("title", Math.random().toString(), false, 2),
            Todo("title", Math.random().toString(), false, 3),
            Todo("title", Math.random().toString(), true, 4),
            Todo("title", Math.random().toString(), false, 5),
        )
    }
}