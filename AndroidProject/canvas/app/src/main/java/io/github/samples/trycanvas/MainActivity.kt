package io.github.samples.trycanvas

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.material.*

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.samples.trycanvas.ui.theme.TryCanvasTheme

enum class TargetState {
    Start, End
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TryCanvasTheme {
//                val infiniteTransition = rememberInfiniteTransition()
//                val angle by infiniteTransition.animateFloat(
//                    initialValue = 0F,
//                    targetValue = 360F,
//                    animationSpec = infiniteRepeatable(
//                        animation = tween(1000, easing = LinearEasing)
//                    )
//                )
                var targetState by remember {
                    mutableStateOf(true)
                }
                Surface(Modifier.fillMaxSize().clickable { targetState = !targetState }) {
                    AnimatedContent(
                        targetState = targetState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(150, 150)) with
                                    fadeOut(animationSpec = tween(150)) using
                                    SizeTransform { initialSize, targetSize ->
                                        if (targetState) {
                                            keyframes {
                                                // Expand horizontally first.
                                                IntSize(targetSize.width, initialSize.height) at 150
                                                durationMillis = 300
                                            }
                                        } else {
                                            keyframes {
                                                // Shrink vertically first.
                                                IntSize(initialSize.width, targetSize.height) at 150
                                                durationMillis = 300
                                            }
                                        }
                                    }
                        }
                    ) { targetExpanded ->
                        if (targetExpanded) {
                            drawOnmyouDama()
                        }
                    }
                }





//                drawOnmyouDama(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .graphicsLayer {
//                            rotationZ = angle
//
//                        }
//
//
//                )
            }
        }
    }
}

//@Composable
//fun MyCanvas() {
//    Canvas(
//        modifier = Modifier
//            .padding(20.dp)
//            .size(300.dp)
//    ) {
//        drawRect(
//            color = Color.Black,
//            size = size
//        )
//
//        drawRect(
//            color = Color.Red,
//            topLeft = Offset(100f, 100f),
//            size = Size(100f, 100f),
//            style = Stroke(
//                width = 3.dp.toPx()
//            )
//        )
//
//        drawCircle(
//            brush = Brush.radialGradient(
//                colors = listOf(Color.Red, Color.Yellow),
//                center = center,
//                radius = 100f
//            ),
//            radius = 100f,
//            center = center
//        )
//
//        drawArc(
//            color = Color.Green,
//            startAngle = 0f,
//            sweepAngle = 270f,
//            useCenter = false,
//            topLeft = Offset(100f, 500f),
//            size = Size(200f, 200f),
//            style = Stroke(
//                width = 3.dp.toPx()
//            )
//        )
//    }
//}
//
@Composable
fun drawOnmyouDama(
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val outsideCircleX = width / 2
        val outsideCircleY = height / 2
        val outsideRadius = width / 2
        val bigRed = Circle(outsideRadius, outsideCircleX, outsideCircleY)
        drawCircle(
            radius = bigRed.radius,
            center = Offset(bigRed.x, bigRed.y),
            color = Color.Red
        )

        val offsetRedToWhite = (width / 64F)
        val baseX = outsideCircleX
        val baseY = outsideCircleY
        val baseRadius = outsideRadius - offsetRedToWhite
        val bigWhite = Circle(baseRadius, outsideCircleX, outsideCircleY)
        drawCircle(
            radius = bigWhite.radius,
            center = Offset(bigWhite.x, bigWhite.y),
            color = Color.White
        )

        val arcTopLeftX = baseX - baseRadius
        val arcTopLeftY = baseY - baseRadius
        drawArc(
            topLeft = Offset(arcTopLeftX, arcTopLeftY),
            size = Size(baseRadius * 2, baseRadius * 2),
            color = Color.Red,
            startAngle = 90f,
            sweepAngle = 180F,
            useCenter = true
        )

        val topMiddleCircleY = baseY - (baseRadius / 2)
        val topMiddleCircleRadius = baseRadius / 2
        drawCircle(
            radius = topMiddleCircleRadius,
            center = Offset(baseX, topMiddleCircleY),
            color = Color.Red
        )
        val bottomMiddleCircleY = baseY + (baseRadius / 2)
        drawCircle(
            radius = topMiddleCircleRadius,
            center = Offset(baseX, bottomMiddleCircleY),
            color = Color.White
        )

        val miniCircleRadius = baseRadius / 10
        drawCircle(
            radius = miniCircleRadius,
            center = Offset(baseX, topMiddleCircleY),
            color = Color.White
        )
        drawCircle(
            radius = miniCircleRadius,
            center = Offset(baseX, bottomMiddleCircleY),
            color = Color.Red
        )
    }
}

data class Circle(val radius: Float, val x: Float, val y: Float)

/*
1) Instagram Icon (Rounded Rectangle + Circle + Gradient Colors)
結構基本、Defaultでなんとかなるものがおおい
 */
@Composable
fun instagramIcon() {
    val instaColors = listOf(Color.Yellow, Color.Red, Color.Magenta)
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(16.dp)
    ) {
        drawRoundRect(
            brush = Brush.linearGradient(colors = instaColors),
            cornerRadius = CornerRadius(60f, 60f),
            style = Stroke(width = 15f, cap = StrokeCap.Round) // Tips 塗り潰さない場合はStyleで仕上げる
        )
        drawCircle(
            brush = Brush.linearGradient(colors = instaColors),
            radius = 45f,
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )
        drawCircle(
            brush = Brush.linearGradient(colors = instaColors),
            radius = 13f,
            center = Offset(this.size.width * .80f, this.size.height * 0.20f),
        )
    }
}

/*
2) Facebook Icon(Rounded Rectangle + Text Custom Font + Color)
カスタムなテキスト配置が特徴
 */
@Composable
fun facebookIcon() {
    val assetManager = LocalContext.current.assets
    val paint = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 200f
        color = Color.White.toArgb()
//        typeface = Typeface.createFromAsset(assetManager, "FACEBOLF.OTF")
    }
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(16.dp)
    ) {
        drawRoundRect(
            color = Color(0xFF1776d1),
            cornerRadius = CornerRadius(20f, 20f)
        )
        drawContext.canvas.nativeCanvas.drawText("f", center.x + 25, center.y + 90, paint)
    }
}

/*
3) Messenger Icon (Path + Oval + Gradient Color)
 */
@Composable
fun messengerIcon() {
    val colors = listOf(
        Color(0xFF02b8f9),
        Color(0xFF0277fe),
    )
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(16.dp)
    ) {

        println("printlnln${10f * .20f} +: ${10f * 0.20f}")
        val trianglePath = Path().let {
            it.moveTo(this.size.width * .20f, this.size.height * .77f)
            it.lineTo(this.size.width * .20f, this.size.height * 0.95f)
            it.lineTo(this.size.width * .37f, this.size.height * 0.86f)
            it.close()
            it
        }

        val electricPath = Path().let {
            it.moveTo(this.size.width * .20f, this.size.height * 0.60f)
            it.lineTo(this.size.width * .45f, this.size.height * 0.35f)
            it.lineTo(this.size.width * 0.56f, this.size.height * 0.46f)
            it.lineTo(this.size.width * 0.78f, this.size.height * 0.35f)
            it.lineTo(this.size.width * 0.54f, this.size.height * 0.60f)
            it.lineTo(this.size.width * 0.43f, this.size.height * 0.45f)
            it.close()
            it
        }

        drawOval(
            Brush.verticalGradient(colors = colors),
            size = Size(this.size.width, this.size.height * 0.95f)
        )

        drawPath(
            path = trianglePath,
            Brush.verticalGradient(colors = colors),
            style = Stroke(width = 15f, cap = StrokeCap.Round)
        )

        drawPath(path = electricPath, color = Color.White)

    }
}

/*
4) Google Photos (Arc + Color)
Tips: とりあえず描いてからOffsetで移動させているのがおもしろい
 */
@Composable
private fun getGooglePhotosIcon() {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(16.dp)
    ) {
        drawArc(
            color = Color(0xFFf04231),
            startAngle = -90f,
            sweepAngle = 180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(size.width * .25f, 0f)
        )
        drawArc(
            color = Color(0xFF4385f7),
            startAngle = 0f, // Tips : Angleは4分円の1,1を0f　時計回りで考える
            sweepAngle = 180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(size.width * .50f, size.height * .25f)
        )
        drawArc(
            color = Color(0xFF30a952),
            startAngle = 0f,
            sweepAngle = -180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(0f, size.height * .25f)
        )

        drawArc(
            color = Color(0xFFffbf00),
            startAngle = 270f,
            sweepAngle = -180f,
            useCenter = true,
            size = Size(size.width * .50f, size.height * .50f),
            topLeft = Offset(size.width * .25f, size.height * .50f)
        )
    }
}
/*
5) iOS Weather App Icon (Cubic + Circle + Rounded Rectangle + Gradient)
Tips: ベジェ曲線
 */
@Composable
fun getWeatherApp() {
    val backgroundColor = listOf(Color(0xFF2078EE), Color(0xFF74E6FE))
    val sunColor = listOf(Color(0xFFFFC200), Color(0xFFFFE100))
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(width.times(.76f), height.times(.72f))
            cubicTo(
                width.times(.93f),
                height.times(.72f),
                width.times(.98f),
                height.times(.41f),
                width.times(.76f),
                height.times(.40f)
            )
            cubicTo(
                width.times(.75f),
                height.times(.21f),
                width.times(.35f),
                height.times(.21f),
                width.times(.38f),
                height.times(.50f)
            )
            cubicTo(
                width.times(.25f),
                height.times(.50f),
                width.times(.20f),
                height.times(.69f),
                width.times(.41f),
                height.times(.72f)
            )
            close()
        }
        drawRoundRect(
            brush = Brush.verticalGradient(backgroundColor),
            cornerRadius = CornerRadius(50f, 50f),

            )
        drawCircle(
            brush = Brush.verticalGradient(sunColor),
            radius = width.times(.17f),
            center = Offset(width.times(.35f), height.times(.35f))
        )
        drawPath(path = path, color = Color.White.copy(alpha = .90f))
    }
}

@Preview
@Composable
fun PP() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            instagramIcon()
            facebookIcon()
            messengerIcon()
            getGooglePhotosIcon()
            getWeatherApp()

        }

    }
}
