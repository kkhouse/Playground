package com.example.composecamerax
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class CustomAnalyzer(private val onValue: (ImageProxy) -> Unit) : ImageAnalysis.Analyzer {
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.use {
            onValue(imageProxy)
        }
    }
}