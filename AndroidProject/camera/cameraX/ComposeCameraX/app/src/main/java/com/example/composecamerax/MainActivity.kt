package com.example.composecamerax

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.composecamerax.ui.theme.ComposeCameraXTheme
import com.google.accompanist.permissions.*


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCameraXTheme {
                /**
                 * カメラ使用するための準備系 インスタンス群
                 */
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    /**
                     * TODO
                     * 正直パーミッションってこれが良さそうだが、通知系ってどうやっているかみてないので
                     * 既存と合うかは不明
                     */
                    val permissionState = rememberMultiplePermissionsState(
                        permissions = listOf(android.Manifest.permission.CAMERA,)
                    )
                    LaunchedEffect(Unit) {
                        permissionState.launchMultiplePermissionRequest()
                    }

                    val value: MutableState<ImageProxy?> =  remember { mutableStateOf(null) }
                    LaunchedEffect(value.value) {
                        Log.d("Analyze", "analyze: ${value.value}")
                    }
                    PermissionsRequired(
                        multiplePermissionsState = permissionState,
                        permissionsNotGrantedContent = { /* TODO */ },
                        permissionsNotAvailableContent = { /* TODO */ }
                    ) {
                        SimpleCameraPreview(
                            CustomAnalyzer { imageProxy -> value.value = imageProxy }
                        )
                    }
                }
            }
        }
    }
}





