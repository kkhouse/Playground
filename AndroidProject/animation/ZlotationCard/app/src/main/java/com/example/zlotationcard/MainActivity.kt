package com.example.zlotationcard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.zlotationcard.ui.theme.ZlotationCardTheme
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CardZIndex(val value: Float) {
    BACK(-1f), FRONT(1f);
}
fun CardZIndex.switch() : CardZIndex {
    return when(this) {
        CardZIndex.BACK -> CardZIndex.FRONT
        CardZIndex.FRONT -> CardZIndex.BACK
    }
}

enum class TranslationY(val value : Float) {
    Default(0f), Top(-300f)
}
fun TranslationY.transit(): TranslationY {
    return when(this) {
        TranslationY.Default -> TranslationY.Top
        TranslationY.Top -> TranslationY.Default
    }
}

enum class RotationX(val value: Float) {
    Front(0f), Back(-180f)
}
fun RotationX.rotate(): RotationX {
    return when(this) {
        RotationX.Front -> RotationX.Back
        RotationX.Back -> RotationX.Front
    }
}

const val DURATION_MS = 1000

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZlotationCardTheme {
                PayPayCardLayout()
            }
        }
    }
}

@Composable
fun PayPayCardLayout() {
    var zIndex by remember { mutableStateOf(CardZIndex.BACK) }
    var translationY by remember { mutableStateOf(TranslationY.Default) }
    var rotationX by remember { mutableStateOf(RotationX.Front) }

    val animateZIndex by animateFloatAsState(
        targetValue = zIndex.value,
        animationSpec = tween(
            durationMillis = DURATION_MS,
            easing = LinearEasing
        ), label = ""
    )
    val animateTransitionY by animateFloatAsState(
        targetValue = translationY.value,
        animationSpec = repeatable(
            iterations = 1,
            animation = tween(
                durationMillis = DURATION_MS/2,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
//        keyframes { // TODO keyframesでもうちょいましなやりかたがありそう
//            durationMillis = DURATION_MS
//            TranslationY.Top.value to (DURATION_MS/2) // アニメーション時間の真ん中でTopに移動
//        }
    )
    val animateRotationX by animateFloatAsState(
        targetValue = rotationX.value,
        animationSpec = tween(
            durationMillis = DURATION_MS,
            easing = LinearEasing
        ), label = ""
    )

    val scope = rememberCoroutineScope()

    PayPayCard(
        cardZIndex = animateZIndex,
        translationY = animateTransitionY,
        rotationX = animateRotationX,
        onCardTap = {
            zIndex = zIndex.switch()
            rotationX = rotationX.rotate()
            translationY = translationY.transit()
            scope.launch {// translationYをリバースしてもどす
                delay((DURATION_MS/2).toLong())
                translationY = translationY.transit()
            }
        }
    )
}

@Composable
fun PayPayCard(
    onCardTap : () -> Unit,
    cardZIndex : Float,
    translationY : Float,
    rotationX : Float,
    cameraDistance: Float = 50f
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer(
                    translationY = translationY,
                    rotationX = rotationX,
                    cameraDistance = cameraDistance,
                )
                .background(Color.White)
                .size(width = 360.dp, height = 280.dp)
                .zIndex(cardZIndex)
                .clickable { onCardTap() }
        ) {
            Text(text = "Card")
        }
        Box(
            modifier = Modifier
                .offset(y = (-24).dp)
                .size(width = 480.dp, height = 200.dp)
                .background(Color.Red)
                .zIndex(0f)
        )
    }
}

@Preview
@Composable
fun PreviewPayPayCardLayout() {
    PayPayCardLayout()
}