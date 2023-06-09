@file:OptIn(ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)

package com.example.animatedcontentplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

data class TargetViewInfo(
    val topLeft: DpOffset = DpOffset(0.dp,0.dp),
    val width: Dp = 0.dp,
    val height: Dp = 0.dp,
    val data: SampleData = SampleData()
)

data class SampleData(
    val title : String = "Title",
    @DrawableRes val img : Int = R.drawable.ic_android_black_24dp
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Cyan)
            ) {
                var isSelected by remember { mutableStateOf(false) }
                var viewData by remember { mutableStateOf(TargetViewInfo()) }
                SampleContainer(
                    onTap = { data , viewPoint ->
                        isSelected = isSelected.not()
                        viewData = viewPoint.copy(data = data)
                    }
                )
                SampleContainerAnimationBox(
                    data = viewData,
                    isVisible = isSelected,
                    onBackPressed = { isSelected = false }
                )
            }
        }
    }
}

@Composable
fun SampleContainer(
    onTap: (SampleData, TargetViewInfo) -> Unit,
) {
    val density = LocalDensity.current
    val list = listOf(SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list) { data ->
            var targetViewInfo by remember { mutableStateOf(TargetViewInfo()) }
            Column(
                modifier = Modifier.clickable { onTap(data, targetViewInfo) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Text(text = data.title)
Box(
modifier = Modifier
.border(width = 1.dp, color = Color.Black)
.size(64.dp)
.onGloballyPositioned {
    with(density) {
        targetViewInfo = targetViewInfo.copy(
            topLeft = DpOffset(
                // TODO Fix : Rootからだと、親Composeの影響を受けそう
                x = it.positionInRoot().x.toDp(),
                y = it.positionInRoot().y.toDp()
            ),
            width = it.size.width.toDp(),
            height = it.size.height.toDp()
        )
    }
}
                        .background(Color.White)
                ){
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(
                            id = R.drawable.ic_android_black_24dp
                        ),
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
    }
}

@Composable
fun SampleContainerAnimationBox(
    data : TargetViewInfo,
    isVisible: Boolean,
    onBackPressed: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
    transitionDuration: Int = 500
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    AnimatedVisibility(
        visible = isVisible,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        /*
        各アニメーション値をAnimatedVisibilityScopeのtransitionで定義することで
        animatedVisibilityの表示非表示のタイミングをアニメーション値の生存期間と一致させる
         */
        val width by transition.animateDp(
            transitionSpec = { tween(durationMillis = transitionDuration) },
            label = "content width"
        ) { state -> if (state == EnterExitState.Visible) screenWidth.dp else data.width }
        val height by transition.animateDp(
            transitionSpec = { tween(durationMillis = transitionDuration) },
            label = "content height"
        ) { state -> if (state == EnterExitState.Visible) screenHeight.dp else data.height }
        val offsetX by transition.animateDp(
            transitionSpec = { tween(durationMillis = transitionDuration) },
            label = "content offset x"
        ) { state -> if (state == EnterExitState.Visible) 0.dp else data.topLeft.x }
        val offsetY by transition.animateDp(
            transitionSpec = { tween(durationMillis = transitionDuration) },
            label = "content offset y"
        ) { state -> if (state == EnterExitState.Visible) 0.dp else data.topLeft.y }
        Box(
            modifier = Modifier
                .size(width = width, height = height)
                .offset(offsetX, offsetY)
                .background(Color.White)
        ) {
            ImageContainerScreen(
                res = data.data.img,
                transitionDuration = transitionDuration
            )
        }
    }

    BackHandler {
        onBackPressed()
    }
}

@Composable
fun ImageContainerScreen(
    @DrawableRes res: Int,
    transitionDuration: Int,
    bottomContent: @Composable () -> Unit = {}
) {
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(3000)
        loading = false
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp),
            painter = painterResource(id = res),
            contentDescription = ""
        )
//        bottomContent()
        Box(modifier = Modifier.fillMaxSize()) {
            when(loading) {
                true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                else -> DummyContent()
            }
        }
    }
}

@Composable
fun DummyContent() {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(durationMillis = 1000))
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat((20)) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "sample content text")
                }
            }
        }
    }
}
