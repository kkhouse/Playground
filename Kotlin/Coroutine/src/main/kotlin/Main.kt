@file:OptIn(DelicateCoroutinesApi::class)

import kotlinx.coroutines.*


fun main(){
//    simpleRunBlocking()
//    singleLaunch()
//    sampleJoin()
//    sampleAsync()
//    sampleAwait()
//    println("-----------")
//    assignmentJob()
//    assignmentJobAndCancel()
//    suspendSample()
//    networkSample()
//    coroutineContextSample()
}

fun simpleRunBlocking() {
    runBlocking { // ビルダー　この中のCoroutineが終了するまで処理を継続
        println("start")
        launch {
            println("World!")
        }
        println("Hello") // 先にこっちが評価される
    }
}

fun singleLaunch() {
    println(1)
    GlobalScope.launch { // いまのThreadをブロックしないので評価前にプログラムが終了
        println(2) // 表示なし
    }
    println(3)
}

fun sampleJoin() {
    runBlocking {
        println(1)
        GlobalScope.launch {
            println(2)
        }.join() // 2の出力（jobの終了）をまってから3が実行
        println(3)
    }
}

fun sampleAsync() {
    println(1)
    GlobalScope.async { // singleLaunch()と一緒
        println(2)
    }
    println(3)
}
/*
コルーチンからなんらかの結果を受け取る場合asyncを使うと思ってよい。
結果がUnitになるコルーチンならlaunchで起動

まあ、ほぼlaunchしか使わない
 */
fun sampleAwait() {
    runBlocking {
        println(1)
        val three = async {
            println(2)
            delay(200)
            3
        }.await()
        println(three)
    }
}

fun assignmentJob() {
    runBlocking {
        val j = launch { // job代入でも中の式は評価される
            println("World!")
        }
        println("Hello")
    }
}

fun assignmentJobAndCancel() {
    runBlocking {
        val j = launch {
            println("World!")
        }
        println("Hello")
        j.cancel() // 評価の速度によるがキャンセルにてWorldは出力されない
    }
}

/*
job: launchで作成したコルーチンのこと。
CoroutineScope: Coroutineを起動するための仮想空間。
CoroutineContext : どこ（Dispatcher）でなに（Job）をやるかの情報

Dispatcher: Coroutineが実行されるスレッドを指定するもの。
    Dispatchers.Main： Mainスレッドを指定。
    Dispatchers.Default: バックグラウンドスレッドを指定。
        CPU負荷の高い処理のとき。(List操作やらJsonパースやら)
    Dispatchers.IO: バックグラウンドスレッドを指定。
        IO(Input/Output)処理の時。(通信、DB)
    Dispatchers.Unconfined
        特定のスレッドに限定されない。（普通は使用しない）
*/


fun suspendSample() {
    runBlocking {
        println("start")
        launch {
            println("World!") // これはThread分けているので評価タイミングはわからない
        }
        suspendFn() // susupend関数を呼び出すと呼び出し元の処理は中断される
        println("Hello")
    }
}
/*
suspendポイントで一度ライブラリ側（kotlinx.coroutinesとかComposeとか）に制御を戻し、然るべきタイミングで再度suspendポイントから続きの処理を再開できる関数。
https://qiita.com/wcaokaze/items/0ecf1c132279ece3d9f6#%E3%81%9D%E3%82%82%E3%81%9D%E3%82%82suspend-fun%E3%81%A3%E3%81%A6%E4%BD%95
 */
suspend fun suspendFn() {
    delay(100)
    println("delay 1s")
}

fun networkSample() {
    println("start")
    val service = getService() // Retrofitの準備とかいろいろ
    runBlocking {
        // getAPiResponseのみThread IO
        when(val result = getApiResponse(service)) {
            is Result.Success -> {
                result.data?.let {
                    println(it)
                }?: kotlin.run { println("response is null") }
            }
            is Result.Failure -> {
                println(result.error)
            }
        }
    }
    println("end")
}
suspend fun getApiResponse(service: Service): Result<SampleResponse?> {
    // Thread IO
    return withContext(Dispatchers.IO) {
        try{
            Result.Success(service.listArticle().body())
        } catch (e: Exception) {
            Result.Failure(e.message!!)
        }
    }
}

//fun coroutineContextSample() {
//    runBlocking {
//        withContext(Dispatchers.Main + Dispatchers.IO) {
//            println("Main: ${this.coroutineContext}")
//        }
//    }
//}

