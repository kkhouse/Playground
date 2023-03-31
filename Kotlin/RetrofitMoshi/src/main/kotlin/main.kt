fun networkSample() {
    println("start")
    val service = getService() // Retrofitの準備とかいろいろ
    runBlocking {
        when(val result = getApiResponse(service)) {
            is Result.Success -> {
                result.data?.let {
                    println(it)
                }?: kotlin.run { println("response is null") }
            }
            is Result.Failure -> {
                println(result.error)
            }
        }
    }
    println("end")
}
suspend fun getApiResponse(service: Service): Result<List<QiitaResItem>?> {
    return withContext(Dispatchers.IO) {
        try{
            Result.Success(service.listArticle().body())
        } catch (e: Exception) {
            Result.Failure(e.message!!)
        }
    }
}