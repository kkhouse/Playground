package io.github.samples.todoapp.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.samples.todoapp.domain.model.Todo

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase: RoomDatabase() {
    abstract val dao: TodoDao
}