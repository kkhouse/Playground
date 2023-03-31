package io.github.samples.gpaysample

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldState
import androidx.compose.material3.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.navigation.*
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class BottomSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    HOME(R.string.home_home, Icons.Outlined.Home, "main/home"),
    PAYMENT(R.string.home_payment, Icons.Outlined.ShoppingCart, "main/payment"),
    PATH(R.string.home_path, Icons.Outlined.AccountCircle, "main/path")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMainState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, coroutineScope) {
    MainState(scaffoldState, navController, coroutineScope)
}

@OptIn(ExperimentalMaterial3Api::class)
class MainState  constructor(
    val scaffoldState: ScaffoldState,
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope
) {
    init {
        // TODO Init SnackBar etc..
        coroutineScope.launch {  }
    }

    // bottomBarState source of truth
    val bottomBarTabs = BottomSections.values()

    // navigation source of truth
    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun NavigateToBottomTab(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                // Pop up backstack to the first destination and save state. This makes going back
                // to the start destination when pressing back in any other bottom tab.
                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    fun changeDrawerState() {
        coroutineScope.launch {
            val drawerState = scaffoldState.drawerState
            if (drawerState.isOpen) drawerState.close() else drawerState.open()

        }
    }
}


private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}