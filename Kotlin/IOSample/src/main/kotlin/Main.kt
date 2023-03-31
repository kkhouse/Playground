import java.io.*
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

fun main(args: Array<String>) {
//    filePathExSample()
    copyStream(
        from = File("/Users/user/dev/Kotlin/Try/IOSample/src/main/kotlin/file.txt"),
        to = File("/Users/user/dev/Kotlin/Try/IOSample/src/main/kotlin/write.txt")
    )
}

fun copyStream(from: File, to: File) {
    try {
        BufferedInputStream(FileInputStream(from)).use { inputStream ->
            BufferedOutputStream(FileOutputStream(to)).use { outputStream ->
                val buffer = ByteArray(1024)
                var bytesRead = 0
                while ((inputStream.read(buffer)).also { bytesRead = it } != -1)
                {
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.flush()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun filePathExSample() {
    println(Paths.get("").absolutePathString())
    val filePath = Paths.get("/Users/user/dev/Kotlin/Try/IOSample/src/main/kotlin/file.txt")
    println("File Path        : $filePath")
    println("Root             : ${filePath.root}")
    println("Parent Directory : ${filePath.parent}")
    println("File Name        : ${filePath.fileName}")
}