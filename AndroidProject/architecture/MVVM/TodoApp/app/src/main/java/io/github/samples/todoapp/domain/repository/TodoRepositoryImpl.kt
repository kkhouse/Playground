package io.github.samples.todoapp.domain.repository

import io.github.samples.todoapp.domain.db.TodoDao
import io.github.samples.todoapp.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val db: TodoDao
): TodoRepository {
    override fun getTodoList(): Flow<List<Todo>> {
        return db.getTodoList()
    }

    override suspend fun deleteTodo(todo: Todo) {
        db.deleteTodo(todo)
    }

    override suspend fun addTodo(todo: Todo) {
        db.addTodo(todo)
    }

    override suspend fun getTodoById(id: Int): Todo {
        return db.getTodoById(id)
    }
}