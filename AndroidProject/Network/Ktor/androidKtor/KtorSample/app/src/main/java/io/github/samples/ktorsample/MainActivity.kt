package io.github.samples.ktorsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.samples.ktorsample.repository.api.Resp
import io.github.samples.ktorsample.repository.api.Service
import io.github.samples.ktorsample.ui.theme.KtorSampleTheme

class MainActivity : ComponentActivity() {
    val service = Service.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val post = produceState<List<Resp>>(
                initialValue = emptyList(),
                producer = {
                    value = service.getPosts()
                }
            )
            println("Success: ${post.value}")
            KtorSampleTheme() {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(post.value) {
                        Text(text = it.title)
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}