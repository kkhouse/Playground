package io.github.samples.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.samples.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.*


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationGraphicsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatorVectorExample()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun AnimatorVectorExample() {
    var atEnd by remember { mutableStateOf(false) }
    val painter = rememberAnimatedVectorPainter(
        AnimatedImageVector.animatedVectorResource(id = R.drawable.animated_vector_assets),
        atEnd
    )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(64.dp)
            .clickable {
                atEnd = !atEnd
            }
    )
}


@ExperimentalAnimationGraphicsApi
@Composable
fun AnimatedVectorDrawableAnim() {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.animated_vector_assets)
    var atEnd by remember { mutableStateOf(false) }
    // This state is necessary to control start/stop animation
    var isRunning by remember { mutableStateOf(true) }
    // The coroutine scope is necessary to launch the coroutine
    // in response to the click event
    val scope = rememberCoroutineScope()
    // This function is called when the component is first launched
    // and lately when the button is pressed
    suspend fun runAnimation() {
        while (isRunning) {
            delay(1000) // set here your delay between animations
            atEnd = !atEnd
        }
    }
    // This is necessary just if you want to run the animation when the
    // component is displayed. Otherwise, you can remove it.
    LaunchedEffect(image) {
        runAnimation()
    }
    Image(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        null,
        Modifier
            .size(150.dp)
            .clickable {
                isRunning = !isRunning // start/stop animation
                if (isRunning) // run the animation if isRunning is true.
                    scope.launch {
                        runAnimation()
                    }
            },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Preview1(title : String = "") {
    Scaffold(
        topBar = { SimpleAppBar1(title = title) },
        content = { Content(innerPadding = it)}
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Preview2() {
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { SimpleAppBar2(scrollBehavior) }
    ) {
        Content(innerPadding = it)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Preview3() {
    val scrollBehavior = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { SimpleAppBar2(scrollBehavior) }
    ) {
        Content(innerPadding = it)
    }
}

@Composable
fun SimpleAppBar1(title: String = "") {
    SmallTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}

@Composable
fun SimpleAppBar2(scrollBehavior: TopAppBarScrollBehavior? = null) {
    SmallTopAppBar(
        title = { Text("Small TopAppBar") },
        navigationIcon = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            // RowScope here, so these icons will be placed horizontally
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun SimpleAppBar3() {}

@Composable
fun Content(innerPadding: PaddingValues) {
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val list = (0..75).map { it.toString() }
        items(count = list.size) {
            Text(
                text = list[it],
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}