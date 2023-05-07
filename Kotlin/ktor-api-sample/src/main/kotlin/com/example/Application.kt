package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import org.jaudiotagger.audio.AudioFileIO
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlinx.serialization.Serializable



//fun main(args: Array<String>): Unit =
//    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureRouting()
}

@Serializable
data class Audio(val sampleRate: Int, val channels: Int, val data: ByteArray)

@OptIn(InternalAPI::class)
fun main(args: Array<String>): Unit {
//    getFileData()
//    val client = HttpClient(CIO)

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    val currentDirPath = Paths.get("").toAbsolutePath()
    val file = "${currentDirPath}/sample.flac"
    val byteArray = ByteArray(File(file).readBytes().size)
    File(file).inputStream().read(byteArray)

    println("$file")

    val path: Path = Paths.get(file)

    // ファイルが存在するかどうかを確認してから削除
    if (Files.exists(path)) {
        println("File '$file' exist.")
    } else {
        println("File '$file' does not exist.")
        throw IllegalAccessError()
    }

    val audio = Audio(44100, 2, byteArray) // 仮の音声データ

    Thread.sleep(1000)
    runBlocking {
        // val response = client.post("http://localhost:8080/upload") {
        //     body = ByteArrayContent(byteArray, ContentType.Audio.Any)
        // }
        val response = client.post("http://localhost:8080/upload") {
            contentType(ContentType.Application.Json)
            setBody(audio)
        }

        println(response.body() as? String)
        println("SIZE ${byteArray.size}")
    }

    Thread.sleep(10000)
}

fun getFileData() {
    val filePath = Path("").absolutePathString() + "/sample.flac"
    val file = File(filePath)
    if (Files.exists(Paths.get(filePath))) {
        println("File '$file' exist.")
    } else {
        println("File '$file' does not exist.")
        throw IllegalAccessError()
    }

    val audioFile = AudioFileIO.read(file)

    println("SampleRate: " + audioFile.audioHeader.sampleRateAsNumber)
    println("Channels: " + audioFile.audioHeader.channels)
}


fun getFlacInfoWithoutLibrary(filePath: String): Pair<Int, Int> {
    val inputStream = FileInputStream(filePath)
    val buffer = ByteArray(1024)
    inputStream.read(buffer)

    val sampleRate = (
            buffer[27].toInt() shl 12 or
                    buffer[28].toInt() shl 4 or
                    buffer[29].toInt() ushr 4
            )
    val channels = ((buffer[21].toInt() ushr 1) and 0x07) + 1

    return Pair(sampleRate, channels)
}

