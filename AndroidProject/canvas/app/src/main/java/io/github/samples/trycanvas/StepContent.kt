package io.github.samples.trycanvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp


@Composable
fun RunStepLayout() {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp)) {
        TopStepContentLayout(triangleHeight = 24.dp){
            Content()
        }
        MiddleStepContentLayout(triangleHeight = 24.dp) {
            Content2()
        }
    }
}

@Composable
fun TopStepContentLayout(
    triangleHeight : Dp = 24.dp,
    content : @Composable () -> Unit,
) {
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    val heightInDp = with(LocalDensity.current) { size.height.toDp() }
    Box {
        TopStepContent(height = heightInDp, triangleHeight)
        Box(modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            }
        ) {
            content()
        }
    }
}

@Composable
fun Content() {
    Column(modifier = Modifier.padding(24.dp)){
        Text(text = "Title",
            style = TextStyle(
                Color.Red
            ),
            textAlign = TextAlign.Center
        )
        Text(text = "hogehogehogehogehogehogehogehogehogehogehogehogehogehoge")
        Text(
            text = "fugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafugafuga"
        )
    }
}
@Composable
fun Content2() {
    Column(modifier = Modifier.padding(24.dp)){
        Text(text = "Title",
            style = TextStyle(
                Color.Red
            ),
            textAlign = TextAlign.Center
        )
        Text(text = "hogehogehogehogehogehogehogehogehogehogehogehogehogehoge")
    }
}
@Composable
fun TopStepContent(
    height : Dp,
    triangleHeight : Dp = 24.dp
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height + triangleHeight)
    ) {
        val testPath = Path().let {
            it.moveTo(0f, 0f)
            it.lineTo(this.size.width, 0f)
            it.lineTo(this.size.width, height.toPx())
            it.lineTo(this.size.width/2, (height + triangleHeight).toPx())
            it.lineTo(0f, height.toPx())
            it.lineTo(0f, 0f)
            it.close()
            it
        }
        drawPath(testPath, color = Color.Black, style = Stroke(width = 1f))
    }
}

@Composable
fun MiddleStepContentLayout(
    triangleHeight: Dp = 24.dp,
    content: @Composable () -> Unit
) {
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    val heightInDp = with(LocalDensity.current) { size.height.toDp() }
    Box{
        MiddleStepContent(height = heightInDp, triangleHeight)
        Box(modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            }
        ) {
            content()
        }
    }
}

@Composable
fun MiddleStepContent(
    height: Dp,
    triangleHeight: Dp = 24.dp,
) {
    val triangleOffset = 12.dp
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height + triangleHeight + triangleHeight)
            .offset {
                IntOffset(
                    x = 0,
                    y = -triangleOffset
                        .toPx()
                        .toInt()
                )
            }
    ) {
        val testPath = Path().let {
            it.moveTo(0f, 0f)
            it.lineTo(this.size.width, 0f)
            it.lineTo(this.size.width, height.toPx())
            it.lineTo(this.size.width / 2, (height + triangleHeight).toPx())
            it.lineTo(0f, height.toPx())
            it.lineTo(0f, 0f)
            it.close()
            it
        }
        drawPath(testPath, color = Color.Black, style = Stroke(width = 1f))
    }
}
