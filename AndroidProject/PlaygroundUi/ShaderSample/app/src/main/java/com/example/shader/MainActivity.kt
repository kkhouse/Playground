package com.example.shader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.shader.ui.theme.ShaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                    }
                }
            }
        }
    }
}


@Composable
fun LightEffect() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                shadowElevation = 32.dp.toPx()
                shape = RoundedCornerShape(16.dp)
                clip = true
                shader = LinearGradientShader(
                    0f,
                    0f,
                    0f,
                    size.height,
                    listOf(Color(0xFFE0E0E0), Color.White),
                    null,
                    ShaderTileMode.Clamp
                )
            }
    )
}