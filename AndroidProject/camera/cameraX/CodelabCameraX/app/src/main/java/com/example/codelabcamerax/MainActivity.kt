package com.example.codelabcamerax

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.codelabcamerax.databinding.ActivityMainBinding
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        Log.d(TAG, "takePhoto imageCapture : $imageCapture")


        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun captureVideo() {}

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.let { cameraFuture ->
            cameraFuture.addListener(
                {// LifeCycleOwnerとカメラのライフサイクルをバインドする
                    val cameraProvider: ProcessCameraProvider = cameraFuture.get()

                    // for binding camera screening data to layout
                    val preview = Preview.Builder().build()
                        .also { it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider) }

                    // Which camera to use on the device
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    // For taking a picture use case
                    imageCapture = ImageCapture.Builder().build()

                    // For analysis preview
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, AnyAnalyzer { format, height, width, cropRect, planes, imageInfo ->
                                Log.d("AnalyzerTag", "height: $height")
                                Log.d("AnalyzerTag", "width: $width")

                                Log.d("AnalyzerTag", "buffer1: ${planes[0].buffer}")
                                Log.d("AnalyzerTag", "buffer1: ${planes[0].rowStride}")
                                Log.d("AnalyzerTag", "buffer1: ${planes[0].pixelStride}")

                                Log.d("AnalyzerTag", "buffer2: ${planes[1].buffer}")
                                Log.d("AnalyzerTag", "buffer2: ${planes[1].rowStride}")
                                Log.d("AnalyzerTag", "buffer2: ${planes[1].pixelStride}")

                                Log.d("AnalyzerTag", "buffer3: ${planes[2].buffer}")
                                Log.d("AnalyzerTag", "buffer3: ${planes[2].rowStride}")
                                Log.d("AnalyzerTag", "buffer3: ${planes[2].pixelStride}")
                            })
                    }

                    try {
                        // Unbind use cases before rebinding
                        cameraProvider.unbindAll()

                        // Bind use cases to camera
                        cameraProvider.bindToLifecycle(
                            this, // lifecycleOwner
                            cameraSelector,  // selector
                            preview, imageCapture, imageAnalyzer // use case
                        )

                    } catch(exc: Exception) {
                        Log.e(TAG, "Use case binding failed", exc)
                    }
                },
                ContextCompat.getMainExecutor(this) // Executor
            )
        }
    }

    private class AnyAnalyzer(private val callback: (Int, Int, Int, Rect, Array<ImageProxy.PlaneProxy>, ImageInfo) -> Unit) : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            image.use {
//                it.setCropRect(Rect(100, 100, 300, 400))
                callback(it.format, it.height, it.width, it.cropRect, it.planes, it.imageInfo)
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
//            Log.e(CUSTOM_TAG, "analyze image.planes.size ${image.planes.size}")
            Log.e(CUSTOM_TAG, "analyze image.format ${image.format}")

            image.use {  }
            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val CUSTOM_TAG = "CameraXAppC"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}