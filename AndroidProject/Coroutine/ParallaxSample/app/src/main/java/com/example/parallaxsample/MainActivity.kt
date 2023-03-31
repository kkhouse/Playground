package com.example.parallaxsample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parallaxsample.ui.theme.ParallaxSampleTheme
import java.lang.Float.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParallaxSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                }
            }
        }
    }
}

/*
Trial Parallax Scroll Behavior

Reference
https://medium.com/proandroiddev/parallax-in-jetpack-compose-bf521244f49
 */
@Preview
@Composable
fun ImageParallaxScroll() {
    val scrollState = rememberScrollState()
    val scrollProvider = { scrollState.value }
    Log.d("ScrollState ", "ImageParallaxScroll: ${scrollProvider()}")
    Log.d("ScrollState ", "ImageParallaxScroll: ${scrollState.maxValue}")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(Color.White)
                .graphicsLayer {
                    // Tips :
                    alpha = 1f - ((scrollProvider().toFloat() / scrollState.maxValue) * 1.5f)
                    translationY = 0.5f * scrollProvider()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.reimu),
                contentDescription = "tiger parallax",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = stringResource(R.string.dummy_text),
            modifier = Modifier.background(
                Color.White
            ),
            style = TextStyle(
                fontSize = 24.sp
            )
        )
    }

    // header
    Box(
        modifier = Modifier
            .alpha(min(1f, (scrollState.value.toFloat() / scrollState.maxValue)))
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.Yellow),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Header bar",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.W900,
                color = Color.Black
            )
        )
    }
}
