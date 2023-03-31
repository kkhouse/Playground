package com.example.composecamerax

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@RequiresApi(Build.VERSION_CODES.P) // TODO
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener(
            {
                continuation.resume(future.get()) // TODO この警告
            },
            mainExecutor
        )
    }
}

@RequiresApi(Build.VERSION_CODES.P) // TODO
suspend fun Context.createAnalyzerUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    executors: ExecutorService,
    previewView: PreviewView,
    onAnalyticsResult: (ImageProxy) -> Unit
): ImageAnalysis {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val analyzer = ImageAnalysis.Analyzer { imageProxy ->
        Log.d("imageProxy", "analyzer: $imageProxy")
        onAnalyticsResult(imageProxy)
    }
    val imageAnalyzer = ImageAnalysis.Builder()
        .build()
        .also { imageAnalysis ->
            imageAnalysis.setAnalyzer(executors, analyzer)
        }

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageAnalyzer
    )

    return imageAnalyzer
}