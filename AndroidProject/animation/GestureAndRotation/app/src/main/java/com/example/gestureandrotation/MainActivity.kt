package com.example.gestureandrotation

import android.os.Bundle
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestureandrotation.DraggableCardConst.maxAngle
import com.example.gestureandrotation.DraggableCardConst.maxAngleSwipeDistance
import com.example.gestureandrotation.SwipingState.Companion.createSwipingState
import com.example.gestureandrotation.SwipingState.Companion.isRightSwiping
import com.example.gestureandrotation.ui.theme.GestureAndRotationTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt


private val TAG = "MainActivityTag"

enum class SwipingState {
    RIGHT, LEFT, CENTER;

    companion object {
        fun SwipingState.isRightSwiping(): Boolean = this == RIGHT

        fun createSwipingState(cardX: Float, offsetX: Float) : SwipingState {
            return when {
                offsetX > cardX -> RIGHT
                offsetX < cardX -> LEFT
                else -> CENTER
            }
        }
    }
}

private data class DraggableCardState(
    val offset: Offset = Offset(x = 0f, y = 0f),
    val isDragEnded: Boolean = false,
    val rotateDegree: Float = 0f,
    val swipingCardText: SwipingCardTextState = SwipingCardTextState(),
    val swipingState: SwipingState = SwipingState.CENTER
) {

    fun createSwipeDistance(cardX: Float): Float {
        return when (swipingState.isRightSwiping()) {
            true -> offset.x - cardX
            else -> cardX - offset.x
        }
    }
    fun createAngleRatio(cardX: Float) : Float {
        return when(swipingState.isRightSwiping()) {
            true -> createSwipeDistance(cardX).coerceIn(0f, maxAngleSwipeDistance).unaryMinus() / maxAngleSwipeDistance
            else -> createSwipeDistance(cardX).coerceIn(0f, maxAngleSwipeDistance) / maxAngleSwipeDistance
        }
    }
    fun createAngleDelta(cardX: Float): Float {
        return createAngleRatio(cardX) * maxAngle.div(2)
    }

    fun createCardLayoutState(cardX: Float): SwipingCardTextState {
        val alpha = abs(createAngleDelta(cardX) / maxAngle).plus(0.4f) // default alpha value
        return when(swipingState) {
            SwipingState.RIGHT -> SwipingCardTextState(
                swipingState = SwipingState.RIGHT, alpha = alpha, alphaText = "RIGHT"
            )
            SwipingState.LEFT -> SwipingCardTextState(
                swipingState = SwipingState.LEFT, alpha = alpha, alphaText = "LEFT"
            )
            SwipingState.CENTER -> SwipingCardTextState(
                swipingState = SwipingState.CENTER, alpha = 0f, alphaText = ""
            )
        }
    }
}

data class SwipingCardTextState(
    val swipingState: SwipingState = SwipingState.CENTER,
    val alphaText: String = "",
    val alpha: Float = 0f
)

object DraggableCardConst {
    val cardHeight = 600.dp // DP
    val cardWidth = 360.dp // DP
    const val maxAngle = 45f
    const val maxAngleSwipeDistance = 1000f
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureAndRotationTheme {
                var reset by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = reset) { reset = false }
                Button(onClick = { reset = true }) { Text(text = "Reset") }
                if(!reset) { SwipableCard() }
            }
        }
    }
}

@Composable
fun SwipableCard() {
    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val maxHalfWidth = this.constraints.maxWidth
        val maxHalfHeight = this.constraints.maxHeight

        with(density) {
            val cardX = (maxHalfWidth/2) - (DraggableCardConst.cardWidth/2).toPx()
            val cardY = (maxHalfHeight/2) - (DraggableCardConst.cardHeight/2).toPx()
            var draggableState by remember { mutableStateOf(DraggableCardState(offset = Offset(cardX, cardY))) }

            val animatedOffsetX by animateFloatAsState(
                targetValue = draggableState.offset.x,
                animationSpec = if(draggableState.isDragEnded) tween(durationMillis = 500) else spring(),//default
                finishedListener = { draggableState = draggableState.copy(isDragEnded = false) }
            )
            val animatedOffsetY by animateFloatAsState(
                targetValue = draggableState.offset.y,
                animationSpec = if(draggableState.isDragEnded) tween(durationMillis = 500) else spring()//default
            )
            val animateRotateDegree by animateFloatAsState(
                targetValue = draggableState.rotateDegree,
                animationSpec = if (draggableState.isDragEnded) tween(durationMillis = 500) else tween(durationMillis = 0)
            )

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
                                draggableState = draggableState.copy(
                                    offset = Offset(
                                        x = draggableState.offset.x + dragAmount.x,
                                        y = draggableState.offset.y + dragAmount.y
                                    ),
                                    rotateDegree = draggableState.createAngleDelta(cardX),
                                    swipingState = createSwipingState(
                                        cardX,
                                        draggableState.offset.x + dragAmount.x
                                    )
                                )
                            },
                            onDragEnd = {
                                draggableState = draggableState.copy(
                                    isDragEnded = true,
                                    offset = when (abs(draggableState.offset.x) < 1000f) {
                                        true -> Offset(x = cardX, y = cardY)
                                        else -> when (draggableState.swipingState.isRightSwiping()) {
                                            true -> Offset(
                                                x = draggableState.offset.x + 500f,
                                                y = draggableState.offset.y + 500f
                                            )
                                            else -> Offset(
                                                x = draggableState.offset.x - 500f,
                                                y = draggableState.offset.y + 500f
                                            )
                                        }
                                    },
                                    rotateDegree = 0f,
                                    swipingState = SwipingState.CENTER,
                                )
                            }
                        )
                    }
                    .graphicsLayer {
                        rotationZ = animateRotateDegree
                    }
                    .size(
                        width = DraggableCardConst.cardWidth,
                        height = DraggableCardConst.cardHeight
                    ),
                textState = draggableState.createCardLayoutState(cardX)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardLayout(
    modifier: Modifier,
    name: String = "Hogehoge",
    image: Int = R.drawable.android_pur,
    textState: SwipingCardTextState = SwipingCardTextState()
) {
    val animateAlpha by animateFloatAsState(targetValue = textState.alpha)
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.LightGray, Color.DarkGray)
                    )
                )
        ) {
            val textBoxSize = Size(width = 120f, height = 80f)
            val rightTextXPos = maxWidth - textBoxSize.width.dp - 60.dp
            val leftTextXPos =  60.dp
            val textYPos = 12.dp

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

            Box(
                modifier = Modifier
                    .size(textBoxSize.width.dp, textBoxSize.height.dp)
                    .offset {
                        when (textState.swipingState) {
                            SwipingState.LEFT -> IntOffset(
                                x = rightTextXPos
                                    .toPx()
                                    .roundToInt(),
                                y = textYPos
                                    .toPx()
                                    .roundToInt()
                            )
                            SwipingState.RIGHT -> IntOffset(
                                x = leftTextXPos
                                    .toPx()
                                    .roundToInt(),
                                y = textYPos
                                    .toPx()
                                    .roundToInt()
                            )
                            SwipingState.CENTER -> IntOffset(0, 0)
                        }
                    }
                    .graphicsLayer {
                        rotationZ = when (textState.swipingState) {
                            SwipingState.RIGHT -> -30f
                            SwipingState.LEFT -> 30f
                            SwipingState.CENTER -> 0f
                        }
                    }
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(animateAlpha),
                    text = textState.alphaText,
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = when(textState.swipingState) {
                            SwipingState.RIGHT -> Color.Green
                            SwipingState.LEFT -> Color.Red
                            SwipingState.CENTER -> Color.Transparent
                        }
                    ),
                    textAlign = when(textState.swipingState) {
                        SwipingState.LEFT -> TextAlign.End
                        SwipingState.RIGHT -> TextAlign.Start
                        SwipingState.CENTER -> null
                    }
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
