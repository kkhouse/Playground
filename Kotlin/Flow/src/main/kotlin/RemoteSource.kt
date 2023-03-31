import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient.Builder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RemoteSource {

    val getTaskResultFlow: Flow<Result<Task>> = flow {
        while(true) { // 宣言しても評価されるのがコンシューマによるcollect時なので無限ループでエラーになることはない
            val task = try {
                getService().listTask().body()?.let {
                    Result.Success(it)
                } ?: Result.Failure("response is null")
            } catch (e: Exception) {
                Result.Failure(e.message ?: "default error message")
            }
            emit(task)
            delay(3000)
        }
    }

    val getTaskExceptionFlow: Flow<Result<Task>> = flow {
        throw object : Exception() {
            override val message: String
                get() = "throw any Exception"
        }
    }
}

sealed class Result<T>  {
    data class Success<T> (val data: T): Result<T>()
    data class Failure<T>(val error: String? = "error"): Result<T>()
}

interface Service {
    @GET("/tasks")
    suspend fun listTask(): Response<Task>
}

fun getService(): Service {
    val okHttpClient = Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    return Retrofit.Builder()
        .baseUrl("http://localhost:8080/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()
        .create(Service::class.java)
}