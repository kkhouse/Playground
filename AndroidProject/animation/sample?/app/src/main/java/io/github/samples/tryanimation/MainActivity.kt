package io.github.samples.tryanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.samples.tryanimation.ui.theme.TryAnimationTheme

private enum class BoxState {
    Small, Big
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TryAnimationTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                ) {
                    changeColor()
                    changeSize()
                    ChangeVisibility()
                    ExpandSample()
                    CrossFadeDemo()
                }
            }
        }
    }
}

@Composable
fun changeColor () {
    var change by remember {
        mutableStateOf(true)
    }
    val color by animateColorAsState(targetValue =  if(change) Color.Blue else Color.Green)
    val t = updateTransition(targetState = change)
    val _color by t.animateColor(transitionSpec = { tween(2000)}) {
        if(it) Color.Blue else Color.Green
    }


    Button(onClick = {
        change = !change
    }
    ) {
        Text("change")
    }

    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 120.dp)
            .padding(bottom = 24.dp)
            .background(_color)
    )
}

@Composable
fun changeSize() {
    var boxState by remember {
        mutableStateOf(BoxState.Small)
    }
    val transition = updateTransition(targetState = boxState)

    val size by transition.animateDp(
        transitionSpec = {
            if(targetState == BoxState.Small) {
                tween(2000)
            } else {
                tween(3000)
            }
        }, label = ""
    ) { state ->
        when(state) {
            BoxState.Small -> 120.dp
            BoxState.Big -> 240.dp
        }
    }

    Button(onClick = {
        boxState = if (boxState == BoxState.Small) BoxState.Big else BoxState.Small
    }
    ) {
        Text("change")
    }

    Box(
        modifier = Modifier
            .size(width = size, height = size)
            .padding(bottom = 24.dp)
            .background(Color.LightGray)
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChangeVisibility() {
    var change by remember { mutableStateOf(true)}
    Button(onClick = {
        change = !change
    }
    ) {
        Text("change")
    }
    AnimatedVisibility(
        change,
        enter = fadeIn(),
        exit = shrinkOut()
    ) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 120.dp)
                .padding(bottom = 24.dp)
                .background(Color.LightGray)
        )
    }
}

@Composable
fun ExpandSample() {
    var change by remember { mutableStateOf(true)}
    Button(onClick = {
        change = !change
    }
    ) {
        Text("change")
    }
    Box(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .background(Color.LightGray)
            .animateContentSize()
    ) {
        Text(
            text = "hogehogheohgoehoaghediaojsinaafdadlnfklawengiovjrmedakovcxgojpkrwmoapcdsknllgjivemofdlkafgjckdmwoakslngijvfmodalkfcodsal",
            maxLines = if (change) 1 else 10,
        )
    }
}

@Composable
fun CrossFadeDemo() {
    var cross by remember {
        mutableStateOf(BoxState.Small)
    }

    Button(onClick = {
        when(cross) {
            BoxState.Small -> cross = BoxState.Big
            BoxState.Big -> cross = BoxState.Small
        }
    }
    ) {
        Text("change", )
    }

    Crossfade(targetState = cross) { cross ->
        when(cross) {
            BoxState.Small -> Text(text = "hello",modifier = Modifier.padding(bottom = 24.dp))
            BoxState.Big -> Text(text = "world",modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}
