package com.example.nestedscroll

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

enum class ScrollingState { Up , Down , NotScrolled }

class DefaultListAwareScrollConnection(
    var minOffset: Float,
    var maxOffset: Float,
    val offsetState: MutableState<Float>,
    val listState: LazyListState,
    val scrollingState: MutableState<ScrollingState> = mutableStateOf(ScrollingState.NotScrolled)
): NestedScrollConnection {

    companion object {
        val TAG: String = DefaultListAwareScrollConnection::class.java.simpleName
    }
    override fun onPreScroll (available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y
        if (delta == 0f) return super.onPreScroll (available, source) // 縦スクロールしてない場合はOverrideしない
        scrollingState.value = if (delta< 0) ScrollingState.Up else ScrollingState.Down

        Log.d(TAG, "currentOffset = ${offsetState.value}")
        // minOffset < currentOffset < maxOffset の時 LazyColumnのイベントを横取り
        return if ((minOffset < offsetState.value) && (offsetState.value < maxOffset)) {
            Log.d(TAG, "offset is within threshold")
            // offset も更新する
            offsetState.value = (offsetState.value + delta).coerceIn(minOffset, maxOffset)
            // yのイベントだけ消費
            Offset(x =  0f, y = available.y)
        } else if (offsetState.value == maxOffset) {
            if (scrollingState.value  == ScrollingState.Up) {
                Log.d(TAG, "offset is MAX and UP")
                // イベント横取り、 offset 更新
                offsetState.value =
                    (offsetState.value + delta).coerceIn(minOffset, maxOffset)
                Offset(x = 0f, y = available.y) // y軸のイベントだけ消費
            } else {
                Log.d(TAG, "offset is MAX and DOWN")
                Offset.Zero
            }
        } else {
            if (scrollingState.value == ScrollingState.Up) {
                Log.d(TAG, "offset is MIN and UP")
                Offset.Zero // LazyColumnの処理優先でoffsetの更新なし
            } else {
                Log.d(TAG, "offset is MIN and DOWN")
                Log.d(TAG, "firstVisibleIndex ${listState.firstVisibleItemIndex}")
                Log.d(TAG, "firstVisibleOffset = ${listState.firstVisibleItemScrollOffset}")
                // LazyColumnの処理を優先, offset更新しない
                when {
                    // Pattern1. LazyColumn@firstIndex
                    listState.firstVisibleItemIndex != 0 -> {
                        Offset.Zero
                    }
                    // Pattern2. LazyColumnのfirstIndexが0だが、 まだ完全にスクロールが終わっていない時
                    listState.firstVisibleItemIndex == 0 &&
                            (listState.firstVisibleItemScrollOffset != 0) -> {
                        Offset.Zero
                    }
                    // Pattern3. 上記以外
                    else -> {
                        offsetState.value = (offsetState.value + delta).coerceIn(minOffset, maxOffset)
                        Offset(x = 0f, y = available.y)
                    }
                }
            }
        }
    }
}