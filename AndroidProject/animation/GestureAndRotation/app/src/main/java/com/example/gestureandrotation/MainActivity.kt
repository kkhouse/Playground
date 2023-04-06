package com.example.gestureandrotation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring.DampingRatioMediumBouncy
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestureandrotation.ui.theme.GestureAndRotationTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureAndRotationTheme {
                val density = LocalDensity.current
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val maxWidth = this.constraints.maxWidth
                    val maxHeight = this.constraints.maxHeight
                    val cardHeight = 600.dp
                    val cardWidth = 360.dp
                    with(density) {
                        val cardX = (maxWidth/2) - (cardWidth/2).toPx()
                        val cardY = (maxHeight/2) - (cardHeight/2).toPx()

                        var offsetX by remember { mutableStateOf(cardX) }
                        var offsetY by remember { mutableStateOf(cardY) }
                        val animatedOffsetX by animateFloatAsState(
                            targetValue = offsetX,
                            animationSpec =  tween(durationMillis = 100)
                        )
                        val animatedOffsetY by animateFloatAsState(
                            targetValue = offsetY,
                            animationSpec = tween(durationMillis = 100)
                        )
                        var rotateDegree by remember { mutableStateOf(0f) }

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
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            offsetX += dragAmount.x
                                            offsetY += dragAmount.y
                                            rotateDegree = if(dragAmount.x>0) 30f else -30f
                                        },
                                        onDragEnd = {
                                            offsetX = cardX
                                            offsetY = cardY

                                            rotateDegree = 0f
                                        }
                                    )
                                }
                                .graphicsLayer {
                                    rotationZ = rotateDegree
                                }
                                .size(width = cardWidth, height = cardHeight)
                        )

                        Text(
                            modifier = Modifier.fillMaxSize().align(Alignment.BottomCenter),
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
