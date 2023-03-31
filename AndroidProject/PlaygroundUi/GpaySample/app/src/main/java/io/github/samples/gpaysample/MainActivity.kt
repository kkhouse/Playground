package io.github.samples.gpaysample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import io.github.samples.gpaysample.ui.theme.GpaySampleTheme
import io.github.samples.gpaysample.vm.HomeViewModelImpl
import io.github.samples.gpaysample.vm.provideHomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GpaySampleTheme {
                ProvideViewModels {
                    GpayApp(rememberMainState())
                }
            }
        }
    }
}

@Composable
fun ProvideViewModels(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        provideHomeViewModelFactory { HomeViewModelImpl() },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpayApp(
    mainState: MainState
) {
    val scaffoldState = mainState.scaffoldState
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = { GpayDrawerContent(mainState = mainState) },
        topBar = { GpayAppBar(mainState = mainState) },
        bottomBar = { GpayBottomBar(mainState) }
    ) { paddingValues ->
        NavHost(
            navController = mainState.navController,
            startDestination = MainDestinations.MAIN_ROUTE,
            modifier = Modifier.padding(paddingValues)
        ) {
            gpayMainNavGraph()
        }
    }
}

fun NavGraphBuilder.gpayMainNavGraph() {
    navigation(
        route = MainDestinations.MAIN_ROUTE,
        startDestination = BottomSections.HOME.route
    ) {
        bottomNavGraph()
    }
}

fun NavGraphBuilder.bottomNavGraph() {
    composable(route = BottomSections.HOME.route) {
        Home()
    }
    composable(route = BottomSections.PAYMENT.route) {
        Payment()
    }
    composable(route = BottomSections.PATH.route) {
        Path()
    }
}

object MainDestinations {
    const val MAIN_ROUTE = "main"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpayDrawerContent (
    mainState: MainState
) {
    Text(text = "sample",
        modifier = Modifier
            .clickable { mainState.changeDrawerState() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpayAppBar (
    mainState: MainState
) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.gpay_acceptance_mark),
                contentDescription = "gpay",
                modifier = Modifier.size(80.dp, 160.dp)
            )},
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "memu",
                modifier = Modifier
                    .graphicsLayer { } // TODO rotate
                    .clickable { mainState.changeDrawerState() }
            )
        },
        actions = {
            Icon(imageVector = Icons.Default.Home, contentDescription = "")
        }
    )
}

@Composable
fun GpayBottomBar(
    mainState: MainState
) {
    NavigationBar() {
        NavigationBarItem(
            selected = true,
            onClick = { mainState.NavigateToBottomTab(BottomSections.HOME.route) },
            icon = { Icon(imageVector = BottomSections.HOME.icon, contentDescription = "")},
            label = { Text(text = stringResource(id = BottomSections.HOME.title))}
        )
        NavigationBarItem(
            selected = true,
            onClick = { mainState.NavigateToBottomTab(BottomSections.PATH.route) },
            icon = { Icon(imageVector = BottomSections.PAYMENT.icon, contentDescription = "")},
            label = { Text(text = stringResource(id = BottomSections.PAYMENT.title))}
        )
        NavigationBarItem(
            selected = true,
            onClick = { mainState.NavigateToBottomTab(BottomSections.PAYMENT.route) },
            icon = { Icon(imageVector = BottomSections.PATH.icon, contentDescription = "")},
            label = { Text(text = stringResource(id = BottomSections.PATH.title))}
        )
    }
}



