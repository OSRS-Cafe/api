package cafe.osrs.api

import kotlin.reflect.typeOf

object APIConfig {
    val host: String = env(name = "API-HOST", default = "0.0.0.0")
    val port: Int = env(name = "API-PORT", default = 8080)
    val tokenWeight: Int = env(name = "API-TOKEN-WEIGHT", default = 100)
    val tokenBucket: Int = env(name = "API-TOKEN-BUCKET", default = 6_000)
    val tokenBucketRefill: Int = env(name = "API-TOKEN-BUCKET-REFILL", default = 60)
    val userAgent: String = env(name = "API-USER-AGENT")
    val hiscoreCacheTimeMinutes: Int = env(name = "HS-CACHE-TIME-MINUTES", default = 10)
    val geCacheTimeMinutes: Int = env(name = "GE-CACHE-TIME-MINUTES", default = 1)
    val playerCountCacheTimeSeconds: Int = env(name = "PLAYER-COUNT-CACHE-SECONDS", default = 30)
    val volumePath: String = env(name = "API-VOLUME-PATH")

    private inline fun <reified T> env(name: String, default: T? = null): T {
        //TODO: Allow enums here
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