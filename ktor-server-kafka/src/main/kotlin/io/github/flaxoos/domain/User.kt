package io.github.flaxoos.domain

import com.sksamuel.avro4k.AvroNamespace
import kotlinx.serialization.Serializable

@Serializable
@AvroNamespace("flaxoos.github.io.domain")
data class User(val id: String, val username: String)
