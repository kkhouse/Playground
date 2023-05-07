package com.example.r

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ConversationButton(
    onSpeakingEnd: (String) -> Unit,
) {
    FloatingActionButton(
        onClick = { /*TODO*/ },
        modifier = Modifier.size(80.dp)
    ) {

    }
}

/**
 * @param amplitude: 振幅 0f ~ 1.0f
 */
@Composable
fun SoundWaveView(amplitude: Float, modifier: Modifier = Modifier) {
    val waveAnimation = rememberInfiniteTransition()
    val scale by waveAnimation.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(32.dp)
            .border(
                shape = CircleShape,
                width = 4.dp,
                color = Color.White,
            )
    ) {
        val waveHeight = (amplitude * 100).coerceIn(0f, 100f) // 振幅は0~1の範囲なので、0~100に変換する
        val waveColor = if (amplitude > 0.5f) Color.Red else Color.Yellow // 振幅が大きいほど色を変える
        val waveWidth = 48.dp

        Wave(
            height = waveHeight,
            width = waveWidth,
            color = waveColor,
            scale = scale
        )
    }
}

@Composable
fun Wave(
    height: Float,
    width: Dp,
    color: Color,
    scale: Float,
    modifier: Modifier = Modifier,
) {
//    val path = remember { Path() }
    Canvas(
        modifier = modifier
            .size(width, height.dp)
            .padding(end = 8.dp)
    ) {
        drawPath(
            path = Path().also {path ->
                path.reset()

                path.moveTo(0f, 0f)
                path.lineTo(0f, height)
                path.quadraticBezierTo(
                    width.toPx() * 0.25f,
                    height * (1 - scale),
                    width.toPx() * 0.5f,
                    height
                )
                path.quadraticBezierTo(
                    width.toPx() * 0.75f,
                    height * (1 + scale),
                    width.toPx(),
                    height
                )
                path.lineTo(width.toPx(), 0f)
                path.close()
            },
            color = color,
            style = Stroke(width = 0f)
        )
    }
}