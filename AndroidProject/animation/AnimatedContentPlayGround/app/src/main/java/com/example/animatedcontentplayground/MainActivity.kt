@file:OptIn(ExperimentalAnimationApi::class)

package com.example.animatedcontentplayground

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

data class ViewPoint(
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
                    .padding(12.dp)
                    .background(Color.Cyan)
            ) {
                var isSelected by remember { mutableStateOf(false) }
                var viewData by remember { mutableStateOf(ViewPoint()) }
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
    onTap: (SampleData, ViewPoint) -> Unit
) {
    val density = LocalDensity.current
    val list = listOf(SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),SampleData(),)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list) { data ->
            var viewPoint by remember { mutableStateOf(ViewPoint()) }
            Column(
                modifier = Modifier.clickable { onTap(data, viewPoint) },
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
                                viewPoint = viewPoint.copy(
                                    topLeft = DpOffset(
                                        // TODO よくわからないが、周辺パディングの影響を受ける様子。ベタがきで引いておく。
                                        x = it.positionInRoot().x.toDp() - 12.dp,
                                        y = it.positionInRoot().y.toDp() - 12.dp
                                    ),
                                    width = it.size.width.toDp(),
                                    height = it.size.height.toDp()
                                )
                            }
                        }
                        .background(Color.Magenta)
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
    data : ViewPoint,
    isVisible: Boolean,
    onBackPressed: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {}
) {
    var visibilityContent by remember { mutableStateOf(true) }
    LaunchedEffect(isVisible) {
        if (isVisible) { visibilityContent = true }
    }

    if(visibilityContent) {
        var viewData by remember { mutableStateOf(data) }
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val screenHeight = LocalConfiguration.current.screenHeightDp
        val animateWidth by animateDpAsState(targetValue = viewData.width, label = "")
        val animateOffsetX by animateDpAsState(targetValue = viewData.topLeft.x, label = "")
        val animateOffsetY by animateDpAsState(targetValue = viewData.topLeft.y, label = "")
        val animateHeight by animateDpAsState(targetValue = viewData.height, label = "",
            finishedListener = {
                if(!isVisible) { visibilityContent = false }
            }
        )
        Box(
            modifier = Modifier
                .size(width = animateWidth, height = animateHeight)
                .offset(animateOffsetX, animateOffsetY)
                .background(Color.White)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = viewData.data.img),
                contentDescription = ""
            )
//             content()
        }
        LaunchedEffect(key1 = isVisible) {
            if (isVisible) {
                viewData.copy(
                    width = screenWidth.dp, height = screenHeight.dp, topLeft = DpOffset(0.dp,0.dp),
                ).also { viewData = it }
            } else {
                viewData.copy(
                    width = data.width, height = data.height, topLeft = data.topLeft,
                ).also { viewData = it }
            }
        }
    }

    BackHandler {
        onBackPressed()
    }
}