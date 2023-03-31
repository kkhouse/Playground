package io.github.samples.todoapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import io.github.samples.todoapp.domain.db.TodoDatabase
import io.github.samples.todoapp.domain.repository.TodoRepository
import io.github.samples.todoapp.domain.repository.TodoRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): TodoDatabase {
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
     fun provideTodoRepository(db: TodoDatabase) : TodoRepository{
         return TodoRepositoryImpl(db.dao)
     }


}