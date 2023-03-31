package io.github.samples.coroutinesample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import io.github.samples.coroutinesample.ui.theme.CoroutineSampleTheme
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var  repository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = injectRepository()
        setContent {
            var city by remember {
                mutableStateOf(CitiesEnum.Tokyo)
            }
            var items by remember {
                mutableStateOf<Response?>(null)
            }
            CoroutineSampleTheme {
                LaunchedEffect(city) {
                    items = repository.getWhether(city = city)
                }
                Screen(
                    onClickCity = { selectedCity ->
                        city = selectedCity
                    },
                    onClickComplete = {
                        Toast.makeText(applicationContext, city.name, Toast.LENGTH_SHORT).show()
                    },
                    items = items
                )
            }
        }
    }

    private fun injectRepository(): MainRepository {
        return MainRepository(
            HttpClient(Android) {
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.INFO
                }
                install(JsonFeature) {
                    serializer = KotlinxSerializer()
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 5000
                    socketTimeoutMillis = 5000
                    connectTimeoutMillis = 5000
                }
            }
        )
    }

    @Composable
    fun Screen(
        onClickCity :(CitiesEnum)-> Unit,
        onClickComplete :()-> Unit,
        items :Response?
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "お天気アプリ",
                modifier = Modifier
                    .padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { onClickCity(CitiesEnum.Tokyo) }
                ){
                    Text(text = "東京")
                }
                Button(onClick = { onClickCity(CitiesEnum.Okinawa) }
                ) {
                    Text(text = "沖縄")
                }
            }
            Items(item = items)

//            Box(modifier = Modifier
//                .fillMaxSize()
//                .padding(bottom = 32.dp)) {
//                Button(
//                    onClick = { onClickComplete() },
//                    modifier = Modifier.align(alignment = Alignment.BottomCenter))
//                {
//                    Text(text = "Compose", ) // 右下
//                }
//            }
        }
    }
    
    @Composable
    fun Items(item: Response?) {
        if(item == null) {
            Text(text = "Error")
            return
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.name)
        Text(text = item.weather[0].main)
        Text(text = item.main.temp.toString())
        
    }
    

    enum class CitiesEnum {
        Tokyo,
        Okinawa
    }
}
