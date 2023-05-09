package com.example.multiplatformintro.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.multiplatformintro.DatabaseDriverFactory
import com.example.multiplatformintro.RocketLaunch
import com.example.multiplatformintro.SpaceXSDK

class MainActivity : ComponentActivity() {
    private val sdk = SpaceXSDK(DatabaseDriverFactory(this))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var errorText: String? by remember { mutableStateOf(null) }
                    val context = LocalContext.current
                    var result : List<RocketLaunch>? by remember { mutableStateOf(null) }
                    LaunchedEffect(key1 = Unit) {
                        kotlin.runCatching { sdk.getLaunches(false) }
                            .onSuccess { result = it }
                            .onFailure {
                                errorText = it.toString()
                                Toast.makeText(context, "Error ${it}",Toast.LENGTH_LONG).show()
                            }
                    }
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)) {
                        errorText?.let {
                            Text(text = "Error is : $it")
                        } ?: run {
                            result?.let { list ->
                                LazyColumn {
                                    items(list) {
                                        Card {
                                            Column(modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                Text(text = it.missionName)
                                                Text(text = it.details ?: "")
                                            }
                                        }
                                    }
                                }
                            } ?: run {
                                CircularProgressIndicator(modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(120.dp))
                            }
                        }
                    }

                }
            }
        }
    }
}
