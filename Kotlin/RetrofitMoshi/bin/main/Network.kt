import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit


sealed class Result<T>  {
    data class Success<T> (val data: T): Result<T>()
    data class Failure<T>(val error: String = "error"): Result<T>()
}

interface Service {
    @GET("/api/v2/items")
    suspend fun listArticle(): Response<List<QiitaResItem>>
}

fun getService(): Service {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(CustomJsonAdapter.FACTORY)
        .build()
    return Retrofit.Builder()
        .baseUrl("https://qiita.com/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()
        .create(Service::class.java)
}

class CustomJsonAdapter (private val moshi: Moshi) : JsonAdapter<List<QiitaResItem?>>() {

    override fun fromJson(reader: JsonReader): List<QiitaResItem?> {
        reader.beginArray()
        val list = arrayListOf<QiitaResItem?>()
        val adapter = moshi.adapter(QiitaResItem::class.java)
        while (reader.hasNext()) {
            list.add(adapter.fromJson(reader))
        }
        reader.endArray()
        return list
    }

    override fun toJson(writer: JsonWriter, value: List<QiitaResItem?>?) {
    }

    companion object {
        val FACTORY: Factory = Factory { type, _, moshi ->
            val listType = Types.newParameterizedType(ArrayList::class.java, QiitaResItem::class.java)
            when(type) {
                listType -> CustomJsonAdapter(moshi)
                else -> null
            }
        }
    }
}