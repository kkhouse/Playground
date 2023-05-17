package com.example.piechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.piechart.ui.theme.PieChartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PieChartTheme {

                var progressCount : Int? by remember { mutableStateOf(null) }
                var progressCount1 : Int? by remember { mutableStateOf(null) }
                var progressCount2 : Int? by remember { mutableStateOf(null) }
                var progressCount3 : Int? by remember { mutableStateOf(null) }
                var progressCount4 : Int? by remember { mutableStateOf(null) }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { PieChart(progressCount = progressCount) }
                    item { PieChart(progressCount = progressCount1) }
                    item { PieChart(progressCount = progressCount2) }
                    item { PieChart(progressCount = progressCount3) }
                    item { PieChart(progressCount = progressCount4) }

                    item {
                        Button(onClick = {
                            progressCount = 0
                            progressCount1 = 1
                            progressCount2 = 2
                            progressCount3 = 3
                            progressCount4 = 4
                        }) {
                            Text(text = "Start")
                        }
                    }

                    item {
                        Button(onClick = {
                            progressCount = null
                            progressCount1 = null
                            progressCount2 = null
                            progressCount3 = null
                            progressCount4 = null
                        }) {
                            Text(text = "Init")
                        }
                    }
                }
            }
        }
    }
}

private const val StartPoint = 0f
private const val FullCircle = 360f
private const val ThreeQuarterPoint = 278f
private const val HalfPoint = 180f
private const val QuarterPoint = 90f
private val startRed = Color(0xFFF22C2C)
private val aQuarter = Color(0xFFE02828)
private val halfRed = Color(0xFFCC2525)
private val threeQuarter = Color (0xFFB72121)
private val endRed = Color(0xFFA61E1E)
private val startGreen = Color(0xFF00C681)
private val endGreen = Color( 0xFF007A58)
private const val circleAnimDuration = 2000
private fun Int?.isInitPosition(): Boolean = this== null
private enum class CircleState(val startColor: Color, val endColor: Color) {
    LOADING(startColor = Color.Transparent, endColor =  Color.Transparent), // Loading
    ALLRed(startColor= startRed, endColor = endRed), // NG
    ALLGreen(startColor= startGreen, endColor = endGreen), // OK
    AQuarter(startColor= startRed, endColor = aQuarter), // 1/4 NG
    Half(startColor= startRed, endColor= halfRed), // Half OK
    ThreeQuarter(startColor= startRed, endColor = threeQuarter); // 3/4 NG
    companion object {
        fun createCircleStatus(completedStatusCount: Int?): CircleState {
            return when (completedStatusCount) {
                4 -> ALLGreen
                3 -> AQuarter
                2 -> Half
                1 -> ThreeQuarter
                0 -> ALLRed
                else -> LOADING
            }
        }
    }
}

/*
progressCount は null ~ 4の間
NOTE: このカウント数を柔軟にできるはず
 */
@Composable
fun PieChart(
    progressCount: Int?,
    contentSize: Dp = 120.dp,
    contentPadding : Dp = 20.dp, // padding分開けた領域に色付きえんが配置される
    backGroundColor : Color = Color.White,
    circleStrokeWidth : Dp = 8.dp
) {
    /*
    各状態等
     */
    var isCircleAnimEnded by remember { mutableStateOf(false) } // アニメーション終了フラグ
    val sweepAngle by animateFloatAsState(
        targetValue = when(progressCount) {
            null -> StartPoint
            1 -> ThreeQuarterPoint
            2 -> HalfPoint
            3 -> QuarterPoint
            else -> FullCircle
        },
        animationSpec = tween( // 初期化時は0sでアニメーションの終点に移動する
            durationMillis = if(progressCount.isInitPosition()) 0 else circleAnimDuration,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { isCircleAnimEnded = true }, label = ""
    )
    // 円グラフ初期化時にアニメーションフラグを初期化する // NOTE このフラグ できれば使いたくない
    LaunchedEffect(sweepAngle < QuarterPoint) { isCircleAnimEnded = false }

    val colorState = CircleState.createCircleStatus(progressCount)
    val circleSize = contentSize - contentPadding

    /*
    Layout
    MEMO ここから下はStatelessComposableに分けられる
     */
    BoxWithConstraints(
        modifier = Modifier
            .size(contentSize)
            .shadow(elevation = 4.dp, shape = CircleShape) // 仕様
            .background(backGroundColor),
        contentAlignment = Alignment.Center
    ) {
        /* 背景の白円 */ // TODO MEMO 上のDrawBehind内に書いた方が自然かも
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = backGroundColor, radius = contentSize.toPx()/2)
        }

        /* 円グラフ */
        Canvas(modifier = Modifier.size(circleSize)) {
            val insideRadius = circleSize.toPx() / 2
            /* Stroke */
            drawCircle(SolidColor(Color.Gray), insideRadius, style = Stroke(circleStrokeWidth.toPx(), cap = StrokeCap.Round))
            /* 赤 or 緑の円 */
            rotate(-90f) { // 開始点を(0,1)へ
                drawArc(
                    brush = Brush.sweepGradient(listOf(colorState.startColor, colorState.endColor)),
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(circleStrokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        /* 始点と終点の丸みを作る */ // NOTE AnimatedVisibilityを二つはいらない気がする。。
        val isEndColorVisible = when(progressCount) {
            0,4 -> isCircleAnimEnded
            else -> false
        }
        AnimatedVisibility(
            visible = isEndColorVisible,
            modifier = Modifier
                .size(circleSize)
                .background(Color.Transparent),
            enter = fadeIn(tween(50)), // この辺の数字は調整の余地あり
            exit = fadeOut(tween(50))
        ) {
            Canvas(modifier = Modifier) {
                val insideRadius = circleSize.toPx() / 2
                rotate(-90f) {
                    drawCircle(
                        radius = (circleStrokeWidth / 2).toPx(),
                        center = Offset(insideRadius * 2, insideRadius),
                        color =  colorState.endColor
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = !isEndColorVisible,
            modifier = Modifier
                .size(circleSize)
                .background(Color.Transparent),
            enter = fadeIn(tween(50)),// この辺の数字は調整の余地あり
            exit = fadeOut(tween(50))
        ) {
            Canvas(modifier = Modifier) {
                val insideRadius = circleSize.toPx() / 2
                rotate(-90f) {
                    drawCircle(
                        radius = (circleStrokeWidth / 2).toPx(),
                        center = Offset(insideRadius * 2, insideRadius),
                        color =  colorState.startColor
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { PieChart(progressCount = null) }
        item { PieChart(progressCount = 0) }
        item { PieChart(progressCount = 1) }
        item { PieChart(progressCount = 2) }
        item { PieChart(progressCount = 3) }
        item { PieChart(progressCount = 4) }
    }
}