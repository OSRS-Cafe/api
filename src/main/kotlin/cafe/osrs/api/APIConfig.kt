package cafe.osrs.api

import kotlin.reflect.typeOf

object APIConfig {
    val host: String = env(name = "API-HOST", default = "0.0.0.0")
    val port: Int = env(name = "API-PORT", default = 8080)
    val tokenWeight: Int = env(name = "API-TOKEN-WEIGHT", default = 100)
    val tokenBucket: Int = env(name = "API-TOKEN-BUCKET", default = 6_000)
    val tokenBucketRefill: Int = env(name = "API-TOKEN-BUCKET-REFILL", default = 60)
    val userAgent: String = env(name = "API-USER-AGENT")

    private inline fun <reified T> env(name: String, default: T? = null): T {
        val envValue = System.getenv(name)
        if(envValue != null) return castTo(envValue)
        if(default != null) return default
        throw Exception("Environment var \"$name\" not set!")
    }

    private inline fun <reified T> castTo(input: String): T {
        return when(val type = typeOf<T>()) {
            typeOf<String>() -> input as T
            typeOf<Int>() -> input.toInt() as T
            else -> throw Exception("Bad env value cast! Unknown type: $type")
        }
    }
}