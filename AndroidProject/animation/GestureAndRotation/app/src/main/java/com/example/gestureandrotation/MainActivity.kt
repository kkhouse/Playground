package com.example.gestureandrotation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestureandrotation.ui.theme.GestureAndRotationTheme
import kotlin.math.roundToInt


private val TAG = "MainActivityTag"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureAndRotationTheme {
                val density = LocalDensity.current
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val maxHalfWidth = this.constraints.maxWidth
                    val maxHalfHeight = this.constraints.maxHeight

                    val cardHeight = 600.dp
                    val cardWidth = 360.dp
                    with(density) {
                        val cardX = (maxHalfWidth/2) - (cardWidth/2).toPx()
                        val cardY = (maxHalfHeight/2) - (cardHeight/2).toPx()

                        var offsetX by remember { mutableStateOf(cardX) }
                        var offsetY by remember { mutableStateOf(cardY) }
                        var isDragEnded by remember{ mutableStateOf(false) }
                        val animatedOffsetX by animateFloatAsState(
                            targetValue = offsetX,
                            animationSpec = if(isDragEnded) tween(durationMillis = 500) else spring(),//default
                            finishedListener = { isDragEnded = false }
                        )
                        val animatedOffsetY by animateFloatAsState(
                            targetValue = offsetY,
                            animationSpec = if(isDragEnded) tween(durationMillis = 500) else spring()//default
                        )
                        var rotateDegree by remember { mutableStateOf(0f) }
                        val animateRotateDegree by animateFloatAsState(
                            targetValue = rotateDegree,
                            animationSpec = if (isDragEnded) tween(durationMillis = 500) else tween(durationMillis = 0)
                        )

                        var startOffsetX by remember { mutableStateOf(0f) }

                        val maxAngle = 45f

                        CardLayout(
                            modifier = Modifier
                                .offset {
                                    IntOffset(
                                        animatedOffsetX.roundToInt(),
                                        animatedOffsetY.roundToInt()
                                    )
                                }
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { startOffsetX = it.x },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            offsetX += dragAmount.x
                                            offsetY += dragAmount.y
                                            val maxSwipe = 1000f
                                            val swipeDistance = when (offsetX >= cardX) {
                                                true -> offsetX - cardX // 右スワイプ
                                                else -> cardX - offsetX // 左スワイプ
                                            }
                                            val angleRatio = when(offsetX >= cardX) {
                                                true -> swipeDistance.coerceIn(0f, maxSwipe) / maxSwipe // 左スワイプ
                                                else -> -( swipeDistance.coerceIn(0f, maxSwipe) / maxSwipe) // 右スワイプ
                                            }

                                            val targetAngle = maxAngle / 2
                                            val angleDelta = targetAngle * angleRatio
                                            rotateDegree = angleDelta
                                        },
                                        onDragEnd = {
                                            isDragEnded = true
                                            offsetX = cardX
                                            offsetY = cardY
                                            rotateDegree = 0f
                                        }
                                    )
                                }
                                .graphicsLayer {
                                    rotationZ = animateRotateDegree
                                }
                                .size(width = cardWidth, height = cardHeight)
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter),
                            text = "posX : $offsetX, posY: $offsetY",
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLayout(
    modifier: Modifier,
    name: String = "Hogehoge",
    image: Int = R.drawable.android_pur
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(
                    colors = listOf(Color.LightGray, Color.DarkGray)
                )
            )
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "background image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = name,
                    style = TextStyle(color = Color.White, fontSize = 24.sp),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    CardLayout(name = "HogeHoge", modifier =Modifier)
}
