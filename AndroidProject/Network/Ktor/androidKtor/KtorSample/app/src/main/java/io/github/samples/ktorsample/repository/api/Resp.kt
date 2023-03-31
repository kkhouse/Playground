package io.github.samples.ktorsample.repository.api

import kotlinx.serialization.Serializable

@Serializable
data class Resp(
    val tags: String,
    val title: String
)