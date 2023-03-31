package io.github.samples.gpaysample

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.github.samples.gpaysample.vm.homeViewModel
import io.github.samples.gpaysample.vm.use

@Composable
fun Home() {
    
    val (state, effect, dispatch) = use(viewModel = homeViewModel())
    
    Text(text = state.content.contentDiscription)

}