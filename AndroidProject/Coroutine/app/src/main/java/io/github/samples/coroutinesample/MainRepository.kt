package io.github.samples.coroutinesample

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*

class MainRepository(
    val client: HttpClient
) {
    private val BASE_URL = "https://pro.openweathermap.org/data/2.5/forecast/"
    private val API_KEY = "17fd5f9e84742e4846f5efbae008c803"
    private fun createUrl(lat:String, lon: String): String {
        return BASE_URL + "climate?lat=${lat}&lon=${lon}&appid=${API_KEY}"
    }
    private fun createUrl(city: MainActivity.CitiesEnum): String {
        return if (city == MainActivity.CitiesEnum.Tokyo) "http://api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=17fd5f9e84742e4846f5efbae008c803" else "https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=17fd5f9e84742e4846f5efbae008c803"
    }

    suspend fun getWhether(city: MainActivity.CitiesEnum): Response? {
        return try {
            val result: Response  = client.get(createUrl(city))
            println("DebugTag api result Success : ${result}")
            return result
        } catch (e: Exception) {
            println("DebugTag api result Errpr : ${e.message}")
            null
        }
    }
}