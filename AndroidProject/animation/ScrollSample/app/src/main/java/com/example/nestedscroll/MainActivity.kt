package com.example.nestedscroll

import android.icu.text.CaseMap.Title
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.nestedscroll.ui.theme.NestedScrollTheme
import kotlin.math.max

data class DummyData(
    val name: String = "text text text"
)
val dummyList = listOf(
    DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),DummyData(),
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NestedScrollTheme {
                val offsetState = remember { mutableStateOf(0f) }
                val listState = rememberLazyListState()
                var barTranslationRange by remember { mutableStateOf(0) }
                var contentAlpha by remember { mutableStateOf(1f) }
                val scrollingState = remember { mutableStateOf(ScrollingState.NotScrolled) }
                val nestedScrollConnection = DefaultListAwareScrollConnection(
                    minOffset = (-barTranslationRange.toFloat()).coerceAtMost(0f),
                    maxOffset = 0f,
                    offsetState = offsetState,
                    listState = listState,
                    scrollingState = scrollingState
                )

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection)
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val (firstBarArea, listContent) = createRefs()

                        TransformingContentBarLayout(
                            modifier = Modifier.constrainAs(firstBarArea) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            },
                            nestedScrollConnection = nestedScrollConnection,
                            firstBarAlphaContent = {
                                /*
                                スクロールで画面外に消えつつ、透過されていく部品
                                 */
                                Column(
                                    modifier = Modifier.alpha(contentAlpha)
                                ) {
                                    Spacer(modifier = Modifier.height(80.dp))
                                    Card(
                                        Modifier
                                            .size(width = 360.dp, height = 240.dp)
                                    ) {
                                        Box(Modifier.fillMaxSize()) {
                                            Text(text = "alpha content", modifier = Modifier.align(Alignment.Center))
                                        }
                                    }
                                }
                            },
                            firstBarNonAlphaContent = {
                                /*
                              スクロールで画面外に消えつつ、透過されない部品
                               */
                                Column(
                                ) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Card(
                                        Modifier
                                            .size(width = 360.dp, height = 24.dp)
                                    ) {
                                        Box(Modifier.fillMaxSize()) {
                                            Text(text = "non alpha content", modifier = Modifier.align(Alignment.Center))
                                        }
                                    }
                                }
                            },
                            iconBar = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                ) {
                                    Text(text = "Title", modifier = Modifier.align(Alignment.Center))
                                }
                            },
                            secondBar = {
                                // 背景
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .background(Color.Red).alpha(0.6f)
                                ){

                                }
                            },
                            firstBarSurfaceBackground = Color.White,
                            offsetState = offsetState,
                            onAlphaRangeConfirmed = {
                                if (offsetState.value >= 0f) {
                                    contentAlpha = 1f
                                } else  {
                                    contentAlpha = (1 + (offsetState.value / it)).coerceIn(0f, 1f)
                                }
                            },
                            onBarTranslationRangeConfirmed = {
                                 barTranslationRange = it.toInt()
                                nestedScrollConnection.minOffset =
                                    (-it).coerceAtMost(0f)
                            },
                            contentAlpha = contentAlpha,
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .constrainAs(listContent) {
                                    top.linkTo(firstBarArea.bottom)
                                    start.linkTo(parent.start)
                                },
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = listState
                        ) {
                            item { Spacer(modifier = Modifier.height(36.dp)) }
                            items(dummyList) {
                                Card (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 120.dp)
                                        .background(Color.Green)
                                ){
                                    Box(modifier = Modifier.fillMaxSize()){
                                        Text(
                                            modifier = Modifier.align(Alignment.Center),
                                            text = it.name,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}