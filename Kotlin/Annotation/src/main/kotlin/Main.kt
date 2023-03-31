import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

fun main() {
    runBlocking {
        api.getPost()
        api.getUser()
    }
}

private val api by lazy {
    Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com")
        .client(
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .addInterceptor(
                    HttpLoggingInterceptor().setLevel(
                    HttpLoggingInterceptor.Level.BODY)
                )
                .build()
        )
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(MyApi::class.java)
}

/**
 * カスタムannotationにつける属性的なアノテーション
 * @Target AnnotationTargetを指定する classes functions など。functionsをつけたAnnotationは関数以外につけられない
 * @Retention バイナリファイルにアノテーションを保持するか と ランタイムからそのアノテーションをみることができるかを指定する
 *  デフォルトはどちらもTrue （　パフォーマンスに関わるが基本的に意識しなくて良いはず。 ）
 * @Repeatable カスタムアノテーションを複数つけられるようにする設定
 * @MustBeDocumented カスタムアノテーションをつけた関数やクラスファイルにドキュメントの作成を強制する
 */

/**
 * 例1
 */
@Target(AnnotationTarget.FIELD)
annotation class AllowedRegex(val regex: String)

data class User(
    val name: String,
    @AllowedRegex("\\d{4}-\\d{2}-\\d{2}") val birthDate: String
) {
    init {
        val fields = this::class.java.declaredFields
        fields.forEach { field ->
            if(field.isAnnotationPresent(AllowedRegex::class.java)) {
                val regex = field.getAnnotation(AllowedRegex::class.java)?.regex
                if(regex?.toRegex()?.matches(birthDate) == false) {
                    throw IllegalArgumentException("Birth date is not " +
                            "a valid date: $birthDate")
                }
            }
        }
    }
}


/**
 * 例2
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Authenticated

class AuthInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val invocation = chain.request().tag(Invocation::class.java)
            ?: return chain.proceed(chain.request())

        val shouldAttachAuthHeader = invocation
            .method()
            .annotations
            .any { it.annotationClass == Authenticated::class }

        return if(shouldAttachAuthHeader) {
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .addHeader("Authorization", "my token")
                    .build()
            )
        } else chain.proceed(chain.request())
    }
}

interface MyApi {

    @GET("/users/1")
    suspend fun getUser()

    @GET("/posts/1")
    @Authenticated
    suspend fun getPost()
}