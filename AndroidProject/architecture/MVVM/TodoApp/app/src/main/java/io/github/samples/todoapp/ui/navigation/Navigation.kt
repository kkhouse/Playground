package io.github.samples.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.samples.todoapp.ui.event.UiEvent
import io.github.samples.todoapp.ui.todo_list.Other
import io.github.samples.todoapp.ui.todo_list.TodoListScreen

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    startDestination: Route = Route.TodoList)
{
    NavHost(
        navController = navController,
        startDestination = startDestination.routePath
    ) {
        composable(route = Route.TodoList.routePath) {
            TodoListScreen({
                navController.navigate(it.route)
            })
        }
        composable(route = Route.Other.routePath) {
            Other(onNavigate = {
                navController.popBackStack()
            })
        }
    }
}

sealed class Route(val name: String, val routePath: String) {
    object TodoList: Route("todoList", "todoList")
    object Other: Route("other", "other")

    companion object {
        fun values() = listOf<Route>(TodoList)
        fun ofRoutePath(routePath: String) = values().find { it.routePath == routePath } ?: TodoList
    }
}

