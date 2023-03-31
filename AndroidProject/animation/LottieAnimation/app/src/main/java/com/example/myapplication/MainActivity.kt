package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "testtesttest"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:MainActivity ")
        setContent {
            MyApplicationTheme {
                val scope = rememberCoroutineScope()
                var isPlaying by remember { mutableStateOf(false) }
                LaunchedEffect (Unit) {
                    scope.launch {
                        kotlinx.coroutines.delay(5000)
                        isPlaying = true
                    }
                }
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.unlock_on))
//                val progress by animateLottieCompositionAsState(composition)
                val progress by animateLottieCompositionAsState(
                    composition,
                    isPlaying = isPlaying
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume:MainActivity ")

    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}