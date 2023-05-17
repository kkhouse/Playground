package com.example.nestedscroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun TransformingContentBarLayout(
    modifier: Modifier = Modifier,
    nestedScrollConnection: DefaultListAwareScrollConnection,
    contentAlpha: Float = 1f,
    firstBarAlphaContent: @Composable () -> Unit,
    firstBarNonAlphaContent: @Composable () -> Unit,
    secondBar: @Composable () -> Unit,
    offsetState: MutableState<Float> = remember { mutableStateOf(0f) },
    onAlphaRangeConfirmed: (Float) -> Unit = {},
    onBarTranslationRangeConfirmed: (Float) -> Unit = {},
    firstBarSurfaceBackground: Color = Color.White,
    iconBar: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
    ) {
        item {
            TransformingContentBar(
                secondBar = secondBar,
                contentAlpha = contentAlpha,
                firstBarAlphaContent = firstBarAlphaContent,
                firstBarNonAlphaContent = firstBarNonAlphaContent,
                offsetState = offsetState,
                onAlphaRangeConfirmed = onAlphaRangeConfirmed,
                onBarTranslationRangeConfirmed = onBarTranslationRangeConfirmed,
                firstBarSurfaceBackground = firstBarSurfaceBackground,
                iconBar = iconBar
            )
        }
    }
}

@Composable
fun TransformingContentBar(
    modifier: Modifier = Modifier,
    secondBarHeight: Dp = 90.dp,
    secondBar: @Composable () -> Unit,
    contentAlpha: Float = 1f, // contentごとアルファを適用すると、 Surfaceのバックグラウンドの色が目立つので使いづらい
    firstBarAlphaContent: @Composable () -> Unit,
    firstBarNonAlphaContent: @Composable () -> Unit,
    offsetState: MutableState<Float> = remember { mutableStateOf(0f) },
    onAlphaRangeConfirmed: (Float) -> Unit = {},
    onBarTranslationRangeConfirmed: (Float) -> Unit = {},
    firstBarSurfaceBackground: Color = Color.White,
    iconBar: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .alpha(1 - contentAlpha)
                    .layoutId("secondBar")
                    .wrapContentSize()
            ) {
                secondBar()
            }

            Box(
                modifier = Modifier
                    // .alpha(contentAlpha)
                    .layoutId("firstBarAlphaContent")
                    .wrapContentSize()
            ) {
                firstBarAlphaContent()
            }
            Box(
                modifier = Modifier
                    .layoutId("firstBarNonAlphaContent")
                    .wrapContentSize()
            ) {
                firstBarNonAlphaContent()
            }
            Box(
                modifier = Modifier
                    .background(firstBarSurfaceBackground)
                    .layoutId("firstBarSurfaceBackground")
                    .fillMaxWidth()
            ) {}

            Box(
                modifier = Modifier
                    .layoutId("iconBar")
            ) {
                iconBar()
            }
        }
    ) { measurables, constraints ->
        // SecondBar
        val secondBarPlaceable = measurables.find { it.layoutId == "secondBar" }?.measure(constraints)
        val alphaFirstBarPlaceable = measurables.find{ it.layoutId == "firstBarAlphaContent" }?.measure (constraints)
        val nonAlphaFirstBarPlaceable = measurables.find{ it.layoutId == "firstBarNonAlphaContent" }?.measure (constraints)
        val iconBarPlaceable = measurables.find { it.layoutId== "iconBar" }?.measure(constraints)
        val secondBarPlaceableHeight = secondBarPlaceable?.height ?: 0
        val alphaFirstBarPlaceableHeight = alphaFirstBarPlaceable?.height ?: 0
        val nonAlphaFirstBarPlaceableHeight = nonAlphaFirstBarPlaceable?.height ?:0
        val maxHeight = alphaFirstBarPlaceableHeight + nonAlphaFirstBarPlaceableHeight
        val alphaRange = (alphaFirstBarPlaceableHeight -  secondBarPlaceableHeight).toFloat().coerceAtLeast(0f)
        onAlphaRangeConfirmed(alphaRange)
        val barTranslationRange = (maxHeight - secondBarPlaceableHeight).toFloat().coerceAtLeast(0f)
        onBarTranslationRangeConfirmed(barTranslationRange)
        val fixedHeight = (maxHeight + offsetState.value.roundToInt())
            .coerceAtLeast (secondBarHeight.roundToPx())

        val firstBarSurfaceBackgroundPlaceable = measurables
            .find { it.layoutId == "firstBarSurfaceBackground" }
            ?.measure(
                Constraints.fixed(
                    width = constraints.maxWidth,
                    // よく分からないが、スワイプで口座情報部が白くならないのでこうしておく
                    height =  0 // (fixedHeight secondBarPlaceableHeight).coerceAtLeast (0)
                )
            )

        /*
        Layout step
        */
        layout(
            width = constraints.maxWidth,
            height = fixedHeight
        ) {
            var posY = 0
            secondBarPlaceable?.placeRelative(
                x = 0,
                y = 0,
                // SecondBarは常に前面でalphaの変化によって出現させる
                zIndex = 2f
            )
            firstBarSurfaceBackgroundPlaceable?.placeRelative(
                x = 0,
                // こうしないと白くならない。。
                y = 0, // secondBarPlaceableHeight
                zIndex = 0f
            )
            alphaFirstBarPlaceable?.placeRelative(
                x = 0,
                y = 0 + offsetState.value.roundToInt(),
                zIndex = 1f
            )
            posY += alphaFirstBarPlaceableHeight
            nonAlphaFirstBarPlaceable?.placeRelative(
                x = 0,
                y = posY + offsetState.value.roundToInt(),
                zIndex = 0f
            )
            iconBarPlaceable?.placeRelative(
                x = 0,
                y = 0,
                zIndex = 2f
            )
        }
    }
}