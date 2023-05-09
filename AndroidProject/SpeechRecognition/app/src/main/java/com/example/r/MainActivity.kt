package com.example.r

import android.Manifest
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment.DIRECTORY_MUSIC
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.RESULTS_RECOGNITION
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.r.ui.theme.RTheme
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


private const val TAG = "Log_Tag_MainActivity"
class MainActivity : ComponentActivity() {

    companion object {
        private const val PERMISSIONS_RECORD_AUDIO = 1000
    }

    private val SAMPLE_RATE = 16000
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isGranted by remember { mutableStateOf(false) }

                    val context = LocalContext.current
                    val requestMultiplePermissions =
                        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                            if (permissions.all { it.value }) {
                                // すべてのパーミッションが許可された場合、ここで処理を行います。
                                isGranted = true
                            } else {
                                // 1つ以上のパーミッションが拒否された場合、ここで処理を行います。
                                throw IllegalAccessError()
                            }
                        }

                    val permissionsNeeded = arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    LaunchedEffect(key1 = Unit) {
                        requestMultiplePermissions.launch(permissionsNeeded)
                    }

                    if(isGranted) {
                        var isRecording by remember {
                            mutableStateOf(false)
                        }
                        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
                        val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize)
                        Column {
                            Button(onClick = {
                                startRecording(audioRecord, bufferSize)
                                isRecording = true
                            }) {
                                Text(text = "StartRecord")
                            }
                        }

                        LaunchedEffect(key1 = isRecording) {
                            if(isRecording) {
                                delay(5000)
                                stopRecording(audioRecord = audioRecord)
                                isRecording = false
                            }
                        }
                    }


//                    val granted = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
//                    var grant by remember { mutableStateOf(granted) }
//                    if (granted != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(this, arrayOf(RECORD_AUDIO), PERMISSIONS_RECORD_AUDIO)
//                    }
//
//                    if(grant ==  PackageManager.PERMISSION_GRANTED) {
//                        SpeechRecognitionScreen(
//                            onSpeechRecognitionResult = {
//
//                            }
//                        )
//                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording(audioRecord: AudioRecord, bufferSize: Int) {


        val outputFile = File(this.getExternalFilesDir(DIRECTORY_MUSIC), "output.wav")
        Log.d(TAG, "startRecording: ${this.getExternalFilesDir(DIRECTORY_MUSIC)}")
        val outputStream = FileOutputStream(outputFile)

        val buffer = ByteArray(bufferSize)
        val header = createWavHeader(bufferSize)

        outputStream.write(header)

        audioRecord.startRecording()

        Thread {
            while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val bytesRead = audioRecord.read(buffer, 0, bufferSize)
                outputStream.write(buffer, 0, bytesRead)
            }
        }.start()
    }

    fun stopRecording(audioRecord: AudioRecord) {
        audioRecord.stop()
        audioRecord.release()
    }

    @Throws(IOException::class)
    private fun createWavHeader(bufferSize: Int): ByteArray {
        val byteRate = SAMPLE_RATE * 2
        val totalDataLen = bufferSize + 36

        val header = ByteArray(44)
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // Sub-chunk size, 16 for PCM
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // Audio format (1 = PCM)
        header[21] = 0
        header[22] = 1 // Number of channels
        header[23] = 0
        header[24] = (SAMPLE_RATE and 0xff).toByte()
        header[25] = (SAMPLE_RATE shr 8 and 0xff).toByte()
        header[26] = (SAMPLE_RATE shr 16 and 0xff).toByte()
        header[27] = (SAMPLE_RATE shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = 2 // Block align (number of channels * bits per sample / 8)
        header[33] = 0
        header[34] = 16 // Bits per sample
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (bufferSize and 0xff).toByte()
        header[41] = (bufferSize shr 8 and 0xff).toByte()
        header[42] = (bufferSize shr 16 and 0xff).toByte()
        header[43] = (bufferSize shr 24 and 0xff).toByte()

        return header
    }

}




@Composable
fun SpeechRecognitionScreen(
    onSpeechRecognitionResult: (String) -> Unit
) {
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }
    val textToSpeech = TextToSpeechFactory.create(context).apply { language = Locale.ENGLISH }
    var normalizedAmplitude by remember { mutableStateOf(0f) }

    val speechRecognizer: SpeechRecognizer by remember {
        mutableStateOf(
            SpeechRecognizerFactory.create(
                context = context,
                recognizerListener = object : RecognitionListener {
                    override fun onReadyForSpeech(p0: Bundle?) {
                        Log.d(TAG, "onReadyForSpeech: ")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(TAG, "onBeginningOfSpeech: ")
                    }

                    override fun onRmsChanged(p0: Float) {
                        Log.d(TAG, "onRmsChanged: p0 $p0")
                        normalizedAmplitude = (p0 + 2) / 12
                    }

                    override fun onBufferReceived(p0: ByteArray?) {
                        Log.d(TAG, "onBufferReceived: ")
                    }

                    override fun onEndOfSpeech() {
                        Log.d(TAG, "onEndOfSpeech: ")
                    }

                    override fun onError(p0: Int) {
                        Log.d(TAG, "onError: $p0")
                    }

                    override fun onResults(p0: Bundle?) {
                        val result = p0?.getStringArrayList(RESULTS_RECOGNITION)
                        Log.d(TAG, "onResults: $result")
                        resultText = result.toString()
                    }

                    override fun onPartialResults(p0: Bundle?) {
                        Log.d(TAG, "onPartialResults: ")
                    }

                    override fun onEvent(p0: Int, p1: Bundle?) {
                        Log.d(TAG, "onEvent: ")
                    }

                }
            )
        )
    }
    var isListening by remember { mutableStateOf(false) }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Tap the button and speak",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                speechRecognizer.startListening(SpeechRecognizerFactory.getRecognizerIntent())
                isListening = true
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Start Listening")
        }

        if (isListening) { Text(text = "Listening...") }

        Button(
            onClick = {
                speechRecognizer.stopListening()
                isListening = false
                textToSpeech.speak("Stop Recording", TextToSpeech.QUEUE_FLUSH, null, null)
        }) {
            Text(text = "Stop Recording")
        }

        Text(text = "Result")
        Text(text = resultText)

        Spacer(modifier = Modifier.height(12.dp))
        SoundWaveView(amplitude = normalizedAmplitude, modifier = Modifier
            .fillMaxWidth()
            .height(40.dp))
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            Log.d(TAG, "DisposableEffect:  ")
            speechRecognizer.destroy()
            textToSpeech.shutdown()
        }
    }
}