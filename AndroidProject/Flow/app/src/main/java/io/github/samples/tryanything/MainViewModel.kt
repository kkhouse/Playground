package io.github.samples.tryanything

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val countDown = flow {
        val start = 5
        var current = start
        emit(current)
        while (current > 0) {
            delay(1000)
            current--
            emit(current)
        }
    }

//    init {
////        collectFlow()
//    }

    private fun collectFlow() {
        val flow1 = flow {
            emit("1")
            emit("2")
            delay(4000L)
            emit("3")
        }
        viewModelScope.launch {
            flow1.onEach { println("ford:: ${it} deriverd")}
                .conflate() // デフォルトはemit →Collectを順次処理。Buffer() はemitは優先的に行うが、collect処理のキューに積まれるイメージ。
                            // conflateは前のcollect処理中に新しいemitが来た場合、collect処理をキャンセルし、新しいemitでcollectする。
                            // デフォルト: 順次、Buffer：emitをバッファする. conflate：新規のemitを優先してcollectする。
                .collect {
                    println("ford::${it} : start")
                    println("ford::${it} : middle")
                    delay(5000L)
                    println("ford::${it} : end")
                }
        }
    }

    private val _stateFlow = MutableStateFlow(0) // stateFlow
    val stateFlow = _stateFlow.asStateFlow()

    fun incrementCounter() {
        viewModelScope.launch {
            _stateFlow.value += 1
            println("forD:: ${stateFlow.value}")
        }
    }

    private val _sharedFlow = MutableSharedFlow<Int>() // stateFlow
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }

    init {
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("ford:: First shared flow is ${it}")

            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("ford:: Second shared flow is ${it}")
            }
        }

        squareNumber(3)
        squareNumber(1)

    }
}