data class SampleResponse(
    val success: Boolean,
    val data: Data
)

data class Data(
    val name: String,
    val note: String,
    val age: Int,
    val registerDate: String
)