package io.github.samples.ktorsample.repository.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*

private val END_POINT = "http://10.81.140.7:8080/book/list"

interface Service {
    suspend fun getPosts(): List<Resp>

    companion object {
        fun create(): Service {
            return ServiceImpl(
                HttpClient(Android) {
                    install(Logging) {
                        logger = Logger.DEFAULT
                        level = LogLevel.HEADERS
                    }
                    install(JsonFeature) {
                        serializer = KotlinxSerializer()
                    }
                    install(HttpTimeout) {
                        requestTimeoutMillis = 1000
                        socketTimeoutMillis = 2000
                        connectTimeoutMillis = 2000
                    }
                }
            )
        }
    }
}

class ServiceImpl(
    private val client: HttpClient
) : Service {
    override suspend fun getPosts(): List<Resp> {
        return try {
            client.get(END_POINT)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            emptyList()
        }
    }
}