package io.github.samples.financedarkthemeany

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.samples.financedarkthemeany.unidirectinalviewmodel.use

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var animate by remember { mutableStateOf(false) }
            RotateIcon(
                state = animate,
                asset = Icons.Default.Home,
                angle = 360f,
                duration = 2000,
                modifier = Modifier.clickable {
                    animate = !animate
                }.size(120.dp)
            )


//            CompositionLocalProvider(
//                provideFeedViewModelFactory { MainViewModelImpl() }
//            ) {
//                FinanceDarkThemeAnyTheme {
//                    Surface(modifier = Modifier.fillMaxSize()) {
//                        Scaffold(
//                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
//                            topBar =  {
//
//                            }
//                            ,
//                            bottomBar = {}
//                        ) {
//                            HomeContent()
//
//                        }
//                    }
//                }
//            }
        }
    }

    @Composable
    fun RotateIcon(
        state: Boolean,
        asset: ImageVector,
        angle: Float,
        duration: Int,
        modifier: Modifier = Modifier
    ) {
        Icon(
            imageVector = asset,
            contentDescription = "",
            modifier = modifier
                .graphicsLayer(
                    rotationZ = animateFloatAsState(if (state) 0f else angle, tween(duration)).value
                )
        )
    }

    @Composable
    fun HomeContent(modifier: Modifier = Modifier) {
        val (state, effect, dispatch) = use(mainViewModel())
        Text(state.mainContentState.text)
    }

    @Composable
    fun TopBar (

    ) {

    }




}






