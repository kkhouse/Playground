@file:OptIn(ExperimentalPagerApi::class)

package com.example.myapplication



import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.graphics.ColorUtils
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "testtesttest"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate:MainActivity ")
        setContent {
            MyApplicationTheme {
//                SampleHorizontalPager()
                AnimContentPager()
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AnimContentPager() {
    val pagerState = rememberPagerState()
    HorizontalPager(
        count = 4,
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(Modifier.fillMaxSize()) {
            Text(
                text = it.toString(),
            )
            when(it) {
                0-> { LottieAnimationComposeSample0(pagerState) }
                1-> { LottieAnimationComposeSample1(pagerState) }
                2-> { LottieAnimationComposeSample2() }
                3-> { LottieAnimationComposeSample3() }
            }
        }
    }
}
private val delayMs = 1000L

@Composable
fun LottieAnimationComposeSample0(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    val isPageShown = pagerState.currentPage == 0
    val animTransMs = 5000L
    var isLottiePlaying by remember { mutableStateOf(false) }
    LaunchedEffect(isPageShown) {
        scope.launch {
            delay(animTransMs)
            isLottiePlaying = true
        }
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.two))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isLottiePlaying
    )
    AnimatedVisibility(
        visible = isPageShown,
        enter = slideIn(tween(1000, easing = LinearOutSlowInEasing)) { fullSize ->
            IntOffset(fullSize.width, fullSize.height)
        }
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
    }
}


@Composable
fun LottieAnimationComposeSample1(pagerState : PagerState) {
    val scope = rememberCoroutineScope()
    val isPageShown = pagerState.currentPage == 1
    val animTransMs = 5000L
    var isLottiePlaying by remember { mutableStateOf(false) }
    LaunchedEffect(isPageShown) {
        scope.launch {
            delay(animTransMs)
            isLottiePlaying = true
        }
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.two))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isLottiePlaying
    )
    AnimatedVisibility(
        visible = isPageShown,
        enter = slideIn(tween(1000, easing = LinearOutSlowInEasing)) { fullSize ->
            IntOffset(fullSize.width, fullSize.height)
        }
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
    }
}

@Composable
fun LottieAnimationComposeSample2() {
    val scope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    LaunchedEffect (Unit) {
        scope.launch {
            kotlinx.coroutines.delay(delayMs)
        }
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.three))
//                val progress by animateLottieCompositionAsState(composition)
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )
}

@Composable
fun LottieAnimationComposeSample3() {
    val scope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    LaunchedEffect (Unit) {
        scope.launch {
            kotlinx.coroutines.delay(delayMs)
            isPlaying = true
        }
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.four))
//                val progress by animateLottieCompositionAsState(composition)
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SampleHorizontalPager() {
    val pagerState = rememberPagerState()
    var pageOffsetLog by remember {
        mutableStateOf(0f)
    }
    Log.d(MainActivity.TAG, "SampleHorizontalPager: $pageOffsetLog")
    Column(Modifier.fillMaxSize()) {
        HorizontalPager(count = 4) { page ->
            Card(
                Modifier
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                        pageOffsetLog = pageOffset
                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                // Card content
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(randomSampleImageUrl(width = 600))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .aspectRatio(1f)
                    )
                    ProfilePicture(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .offset {
                                val pageOffset =
                                    this@HorizontalPager.calculateCurrentOffsetForPage(page)
                                IntOffset(
                                    x = (36.dp * pageOffset).roundToPx(),
                                    y = 0
                                )
                            }
                    )
                }
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            indicatorShape = RoundedCornerShape(0.dp)
        )
    }


//    val scope = rememberCoroutineScope()
//    LaunchedEffect(Unit) {
//        scope.launch {
//            delay(2000)
//            pagerState.scrollToPage(pagerState.currentPage+1)
//        }
//    }
}

@Composable
private fun ProfilePicture(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = CircleShape,
        border = BorderStroke(4.dp, MaterialTheme.colors.surface)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(randomSampleImageUrl())
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )
    }
}



fun randomSampleImageUrl(
    seed: Int = (0..100000).random(),
    width: Int = 300,
    height: Int = width,
): String {
    return "https://picsum.photos/seed/$seed/$width/$height"
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColorSampleHorizontalPager() {
    val colorList = listOf(Color.Red, Color.Green, Color.Blue)

    val pagerState = rememberPagerState()
    val backgroundColor = animateColorAsState(
        setPagerBackgroundColor(pagerState, colorList)
    )
    HorizontalPager(
        count = 3,
        state = pagerState,
        modifier = Modifier
            .background(backgroundColor.value)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Text(
            text = it.toString(),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@ExperimentalPagerApi
private fun setPagerBackgroundColor(
    pagerState: PagerState,
    colorList: List<Color>
): Color {
    Log.d(MainActivity.TAG, "setPagerBackgroundColor: ${pagerState.currentPageOffset}")
    return if (pagerState.isScrollInProgress) {
        if (pagerState.currentPageOffset >= 0) {
            val currentIndex = pagerState.currentPage + floor(pagerState.currentPageOffset).toInt()
            val nextIndex = if (pagerState.currentPage + floor(pagerState.currentPageOffset).toInt() != pagerState.pageCount - 1) {
                pagerState.currentPage + floor(pagerState.currentPageOffset).toInt() + 1
            } else {
                pagerState.currentPage + floor(pagerState.currentPageOffset).toInt()
            }
            Color(
                ColorUtils.blendARGB(
                    colorList[currentIndex].toArgb(),
                    colorList[nextIndex].toArgb(),
                    pagerState.currentPageOffset - floor(pagerState.currentPageOffset)
                )
            )
        } else {
            val currentIndex = pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt()
            val previousIndex = if (pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt() != 0) {
                pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt() - 1
            } else {
                pagerState.currentPage + ceil(pagerState.currentPageOffset).toInt()
            }
            Color(
                ColorUtils.blendARGB(
                    colorList[previousIndex].toArgb(),
                    colorList[currentIndex].toArgb(),
                    pagerState.currentPageOffset - floor(pagerState.currentPageOffset)
                )
            )
        }
    } else {
        colorList[pagerState.currentPage]
    }
}