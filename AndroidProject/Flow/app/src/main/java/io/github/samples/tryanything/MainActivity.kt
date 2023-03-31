package io.github.samples.tryanything

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.samples.tryanything.ui.theme.TryAnythingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TryAnythingTheme {
                val vm = viewModel<MainViewModel>()
                val count = vm.stateFlow.collectAsState()
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Button(onClick = { vm.incrementCounter()}) {
                        Text(text = "count : ${count.value}")
                    }
                }
            }
        }
    }
}