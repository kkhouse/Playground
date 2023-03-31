package io.github.samples.myapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val emittingScope = CoroutineScope(Dispatchers.IO)
    val collectingScope = CoroutineScope(Dispatchers.Default)

    val stateFlow = MutableStateFlow(0)
    fun emitStateData(value : Int) {
        emittingScope.launch {
            println("emitted $value")
            stateFlow.emit(value)
        }
    }
    fun collectStateData() {
        collectingScope.launch {
            stateFlow.collect {

            }
        }
    }

    @Test
    fun flowAnything() {

    }
}