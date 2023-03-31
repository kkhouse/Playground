package io.github.samples.todoapp.domain.repository

import io.github.samples.todoapp.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodoList(): Flow<List<Todo>>
    suspend fun deleteTodo(todo: Todo)
    suspend fun addTodo(todo: Todo)
    suspend fun getTodoById(id: Int): Todo
}