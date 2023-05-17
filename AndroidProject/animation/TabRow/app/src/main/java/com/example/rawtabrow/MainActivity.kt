package com.example.rawtabrow

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rawtabrow.ui.theme.RawTabRowTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RawTabRowTheme {
                var tab by remember { mutableStateOf(TabType.Left) }
                ThreeColumTab({tab = it}, tab)
            }
        }
    }
}


enum class TabType {
    Left, Center, Right
}
@Composable
fun context() = LocalContext.current

fun convertPx2Dp(px: Int, context: Context): Float {
    val metrics: DisplayMetrics = context.resources.displayMetrics
    return px / metrics.density
}
fun Int.toDp(context: Context) = convertPx2Dp(this, context).dp

@Composable
fun ThreeColumTab(
    onTabTapped: (TabType) ->  Unit,
    currentTab: TabType,
) {
    /* 各タブのタップエリア */
    var firstTabAreaSize by remember { mutableStateOf(Size(0f, 0f)) }
    var secondTabAreaSize by remember { mutableStateOf(Size(0f, 0f)) }
    var thirdTabAreaSize by remember { mutableStateOf(Size(0f, 0f)) }
    /* インジケータ アニメーション */
    var indicatorWidth by remember { mutableStateOf(0.dp) }
    val transition = updateTransition(targetState = currentTab, label = "")
    var indicatorLeftTabPos by remember { mutableStateOf(0.dp) }
    var indicatorCenterTabPos by remember { mutableStateOf(0.dp) }
    var indicatorRightTabPos by remember { mutableStateOf(0.dp) }
    val animateIndicatorPos by transition.animateDp(label = "") { tab ->
        when(tab) {
            TabType.Left -> indicatorLeftTabPos
            TabType.Center -> indicatorCenterTabPos
            TabType.Right -> indicatorRightTabPos
        }
    }

    /* 細かな部品のHeight値 */
    val dividerHeight = 2.dp
    val indicatorHeight = 3.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(Color.White)
    ) {
        Layout(
            modifier = Modifier.wrapContentSize(),
            content = {
                Text(
                    modifier = Modifier.layoutId("LeftTabText"),
                    text = "Left Tab"
                )

                Image(
                    modifier = Modifier
                        .layoutId("left_divider"),
                    painter = painterResource(id = R.drawable.baseline_drag_indicator_24),
                    contentDescription = ""
                )

                Text(
                    modifier = Modifier.layoutId("CenterTabText"),
                    text = "Center Tab"
                )

                Image(
                    modifier = Modifier
                        .layoutId("right_divider"),
                    painter = painterResource(id = R.drawable.baseline_drag_indicator_24),
                    contentDescription = ""
                )

                Text(
                    modifier = Modifier.layoutId("RightTabText"),
                    text = "Right Tab Long Text"
                )

                Divider(modifier = Modifier
                    .layoutId("grayLine")
                    .height(dividerHeight))

                Box(
                    modifier = Modifier
                        .layoutId("indicator")
                        .height(indicatorHeight)
                        .width(width = indicatorWidth)
                        .background(Color.Cyan)
                        .animateContentSize()
                )

                Box(
                    modifier = Modifier
                        .layoutId("leftTabArea")
                        .size(
                            width = firstTabAreaSize.width
                                .roundToInt().toDp(context()),
                            height = firstTabAreaSize.height
                                .roundToInt().toDp(context()),
                        )
                        .clickable { onTabTapped(TabType.Left) }
                )
                Box(
                    modifier = Modifier
                        .layoutId("centerTabArea")
                        .size(
                            width = secondTabAreaSize.width
                                .roundToInt().toDp(context()),
                            height = secondTabAreaSize.height
                                .roundToInt().toDp(context()),
                        )
                        .clickable { onTabTapped(TabType.Center) }
                )
                Box(
                    modifier = Modifier
                        .layoutId("rightTabArea")
                        .size(
                            width = thirdTabAreaSize.width
                                .roundToInt().toDp(context()),
                            height = thirdTabAreaSize.height
                                .roundToInt().toDp(context()),
                        )
                        .clickable { onTabTapped(TabType.Right) }
                )
            }
        ) { measures, constraints ->
            /* Measure step */
            val leftText = measures.find { it.layoutId == "LeftTabText" }?.measure(constraints)
            val leftSeparator = measures.find { it.layoutId == "left_divider" }?.measure(constraints)
            val centerText = measures.find { it.layoutId == "CenterTabText" }?.measure(constraints)
            val rightText = measures.find { it.layoutId == "RightTabText" }?.measure(constraints)
            val rightSeparator = measures.find { it.layoutId == "right_divider" }?.measure(constraints)
            val grayDivider = measures.find { it.layoutId == "grayLine" }?.measure(constraints)
            val indicator = measures.find { it.layoutId == "indicator" }?.measure(constraints)
            val leftArea = measures.find { it.layoutId == "leftTabArea" }?.measure(constraints)
            val centerArea = measures.find { it.layoutId == "centerTabArea" }?.measure(constraints)
            val rightArea = measures.find { it.layoutId == "rightTabArea" }?.measure(constraints)

            /* 各 Widthの計算 */
            val leftTextWidth = leftText?.width  ?: 0
            val centerTextWidth = centerText?.width  ?: 0
            val rightTextWidth = rightText?.width  ?: 0
            val separatorWidth = leftSeparator?.width ?: 0
            val textHorizontalPadding = (constraints.maxWidth - leftTextWidth - centerTextWidth - rightTextWidth - separatorWidth - separatorWidth) / 6

            /* heightの計算 */
            val textYPos = 12.dp.toPx().roundToInt() // 仕様
            val leftTextHeight = leftText?.height ?: 0
            val centerTextHeight = centerText?.height ?: 0
            val rightTextHeight = rightText?.height ?: 0
            val textMaxHeight = arrayOf(leftTextHeight,centerTextHeight,rightTextHeight).maxOf { it }
            val contentHeight = 24.dp.toPx() + textMaxHeight // テキストから上下のPadding24 + テキスト自体のHeight

            layout(
                width = constraints.maxWidth,
                height = contentHeight.roundToInt()
            ) {
                val separatorYPos = 14.dp.toPx().roundToInt() // 仕様

                leftText?.placeRelative(x = textHorizontalPadding, y = textYPos)
                val leftSeparatorXPos = textHorizontalPadding + leftTextWidth + textHorizontalPadding
                leftSeparator?.placeRelative(x = leftSeparatorXPos, y = separatorYPos)
                val centerTextXPos = leftSeparatorXPos + separatorWidth + textHorizontalPadding
                centerText?.placeRelative(x = centerTextXPos, y = textYPos)
                val rightSeparatorXPos = centerTextXPos + centerTextWidth + textHorizontalPadding
                rightSeparator?.placeRelative(x = rightSeparatorXPos, y = separatorYPos)
                val rightTextXPos = rightSeparatorXPos + separatorWidth + textHorizontalPadding
                rightText?.placeRelative(x = rightTextXPos, y = textYPos)

                /*
                タップ領域
                 */
                firstTabAreaSize = Size(width = leftSeparatorXPos.toFloat(), contentHeight)
                secondTabAreaSize = Size(width = (rightSeparatorXPos - leftSeparatorXPos - separatorWidth).toFloat(), height = contentHeight)
                thirdTabAreaSize = Size(width = (constraints.maxWidth - rightSeparatorXPos - separatorWidth).toFloat(), height = contentHeight)
                leftArea?.placeRelative(x = 0, y = 0)
                centerArea?.placeRelative(x = leftSeparatorXPos + separatorWidth, y = 0)
                rightArea?.placeRelative(x = rightSeparatorXPos + separatorWidth, y = 0)

                grayDivider?.placeRelative(
                    x = 0,
                    y = contentHeight.roundToInt() - dividerHeight.toPx().roundToInt()
                )
                when(currentTab) {
                    TabType.Left -> {
                        indicatorWidth = leftTextWidth.toDp()
                        indicatorLeftTabPos = textHorizontalPadding.toDp()
                    }
                    TabType.Center -> {
                        indicatorWidth = centerTextWidth.toDp()
                        indicatorCenterTabPos = centerTextXPos.toDp()
                    }
                    TabType.Right -> {
                        indicatorWidth = rightTextWidth.toDp()
                        indicatorRightTabPos = rightTextXPos.toDp()
                    }
                }
                indicator?.placeRelative(
                    x = animateIndicatorPos.toPx().roundToInt(),
                    y = contentHeight.roundToInt() - indicatorHeight.toPx().roundToInt()
                )
            }
        }
    }

}

@Preview
@Composable
fun Preview() {
    ThreeColumTab(
        {}, TabType.Left
    )
}