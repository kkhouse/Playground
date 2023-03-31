@file:OptIn(DelicateCoroutinesApi::class)

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

fun main() {
    println("start")
//    simpleFlowSample()
//    simpleFlowSampleWithCancel()
//    flowSampleGettingRemoteSource()
//    flowSampleWithMap()
//    flowSampleWithException()
//    flowCombineSample()
//    toUpperWithFlow()
    sampleSharedFlowChangingReplay()
    println("end")
}

val numberEmitter = flow {
    emit(1)
    delay(1000)
    emit(2)
    delay(1000)
    emit(3)
}

/*
https://developer.android.com/kotlin/flow?hl=ja#create
- flowとは
    複数の値を順次出力できるもの。概念的にはデータストリームとおなじ。
- プロデューサ: ストリームに出力するデータを生成。
    getTaskResultの中の処理が例、要するにflow { } を作るところ
- （必要な場合）インターミディアリ: ストリームに出力された各値またはストリームそのものを変更できます。
    flowSampleWithMapのMap処理が例、ようするにプロデューサからの値を加工するところ
- コンシューマ: ストリームから得られた値を使用します。
    printFlowResultWithTimeoutが例。ようするにcollectを呼ぶところ

kotlinlang.orgでは出力側を「Emitter」、受け取る側を「Subscriber」と呼んでいる。まあ、どっちでも
 */
fun simpleFlowSample() {
    runBlocking {
        numberEmitter.collect { number ->
            println(number)
        }
    }
}

fun simpleFlowSampleWithCancel() {
    var job: Job? = null
    GlobalScope.launch {
        delay(2000)
        job?.cancel() // CoroutineをキャンセルすればFlowのコンシュームも止まる
    }
    runBlocking {
        job = launch {
            numberEmitter.collect { number ->
                println(number)
            }
        }
    }
}

fun flowSampleGettingRemoteSource() {
    printFlowResultWithTimeout(RemoteSource().getTaskResultFlow)
}

fun flowSampleWithMap() {
    // flow結果に応じて、値の中身をいじりたいときはmap,filter 等ストリームの関数達が使える
    // インターミディアリ
    val flowMapResult = RemoteSource().getTaskResultFlow.map { result ->
        when(result) {
            is Result.Success -> Result.Success(result.data.copy(content = "結果の変更文字列"))
            is Result.Failure -> result
        }
    }
    printFlowResultWithTimeout(flowMapResult)
}

/*
RemoteSource.getTaskResultの中でやっている例外catchはFlowでもできる
 */
fun flowSampleWithException() {
    val resultExceptionFlow = RemoteSource().getTaskExceptionFlow
        .catch { exception -> emit(Result.Failure(exception.message ?: "message is null")) }
    printFlowResultWithTimeout(resultExceptionFlow)
}

/*
Flow<T1>.combine(flow: Flow<T2>, transform: suspend (a: T1, b: T2) -> R): Flow<R>
現在のデータストリームのFlowと別のflowから、同時にcollectすることができる
 */
fun flowCombineSample() {
    val flowResult = RemoteSource().getTaskResultFlow
        .combine(numberEmitter) { taskResult, number ->
            // 複数のFlowを同時にcollectしたい時に便利
            when(number) {
                3 -> {
                    println("success number : $number")
                    Result.Success(taskResult)
                }
                else -> Result.Failure("invalid number : $number")
            }
        }
    printFlowResultWithTimeout(flowResult)
}

/*
インターミディタリに応じてCoroutineContextを切り替えることもできる
flowOnからうえをUpStream、下をDownStreamとう
 */
fun flowSampleWithCoroutineContext() {
    val scope = GlobalScope.coroutineContext
    runBlocking {
        RemoteSource().getTaskResultFlow
            .map { result ->
                when(result) {
                    is Result.Success -> Result.Success(result.data.copy(content = "結果の変更文字列"))
                    is Result.Failure -> result
                }
            }
            .onEach { result ->
            /* if (result is Result.Failure) anyEffect(result) */
            }
            .flowOn(scope) // ここから↑はscopeで、↓はrunBlockingのCoroutineScopeで
            .combine(numberEmitter) { taskResult, number ->
                when(number) {
                    3 -> {
                        println("success number : $number")
                        Result.Success(taskResult)
                    }
                    else -> Result.Failure("invalid number : $number")
                }
            }
            .collect { it.printResultBody() }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <T> printFlowResultWithTimeout(result: Flow<Result<T>>) {
    var job: Job? = null
    GlobalScope.launch {
        delay(10000)
        job?.cancel() // 10s後に止める
    }
    runBlocking {
        job = launch {
            println(result.collect { it.printResultBody() }) // collectで値を取得できる
        }
    }
}

fun <T> Result<T>.printResultBody() {
    when(this) {
        is Result.Success -> println("body : ${this.data}")
        is Result.Failure -> println("error : ${this.error}")
    }
}

// SharedFlow
fun sampleSharedFlowChangingReplay() {
    runBlocking {
        val sampleFlow = MutableSharedFlow<Int>(replay = 1)
        GlobalScope.launch {
            delay(1500)
            sampleFlow.collect {
                print("collect is :$it \n")
            }
        }
        GlobalScope.launch {
            (0..5).forEach {
                delay(1000)
                print("emit value is : $it ")
                sampleFlow.emit(it)
            }
        }
        /*
        replay 0
            emit value is : 0 emit value is : 1 collect is :1
            emit value is : 2 collect is :2
            emit value is : 3 collect is :3
            emit value is : 4 collect is :4
            emit value is : 5 collect is :5

        replay 1
            emit value is : 0 collect is :0
            emit value is : 1 collect is :1
            emit value is : 2 collect is :2
            emit value is : 3 collect is :3
            emit value is : 4 collect is :4
            emit value is : 5 collect is :5

        collect時に、collect前にemitされた値をreplay数分取ってくる
        後から追加されたSubscriberに対して出力するために値を保持しておくバッファ
         */

        delay(10000) // プログラムの終了をさせない
    }
}


// ----- ここから下はいろいろ試してるとこ
fun toUpperWithFlow() {
    val flow = flow {
        emit("a")
        delay(500)
        emit("b")
        delay(500)
        emit("c")
    }

    runBlocking {
        flow
            .map {
                println("flow map : $it")
                it.uppercase()
            }
            .collect {
            println(it)
        }
    }
}

// https://ninjinkun.hatenablog.com/entry/introrxja
// このサジェスト　=>
