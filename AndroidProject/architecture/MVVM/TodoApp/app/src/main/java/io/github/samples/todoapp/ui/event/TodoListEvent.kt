package io.github.samples.todoapp.ui.event

import io.github.samples.todoapp.domain.model.Todo

sealed class TodoListEvent {
    data class onDeleteTodo(val todo: Todo): TodoListEvent()
    data class onDoneTodo(val todo: Todo, val isDone: Boolean): TodoListEvent()
    data class onClickTodo(val todo: Todo): TodoListEvent()
    object onAddTodo: TodoListEvent()
    object onUndoTodoDelete: TodoListEvent()
}