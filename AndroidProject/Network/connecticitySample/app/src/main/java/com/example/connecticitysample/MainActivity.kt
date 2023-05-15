package com.example.connecticitysample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.connecticitysample.ui.theme.ConnecticitySampleTheme
import kotlinx.coroutines.flow.stateIn

class MainActivity : ComponentActivity() {
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = NetworkViewModel(this) // NOTE: Should be DI

            ConnecticitySampleTheme {
                val status = viewModel.networkStatus.collectAsState()
                when(status.value) {
                    NetworkStatus.Connected -> Log.d(TAG, "Connected")
                    NetworkStatus.Disconnected -> Log.d(TAG, "Disconnected")
                    NetworkStatus.Unknown -> Log.d(TAG, "Unknown")
                }
            }
        }
    }
}