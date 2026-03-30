package cafe.osrs.api

import kotlin.reflect.typeOf

object APIConfig {
    val host: String = env(name = "API_HOST", default = "0.0.0.0")
    val port: Int = env(name = "API_PORT", default = 8080)
    val tokenWeight: Int = env(name = "API_TOKEN_WEIGHT", default = 100)
    val tokenBucket: Int = env(name = "API_TOKEN_BUCKET", default = 6_000)
    val tokenBucketRefill: Int = env(name = "API_TOKEN_BUCKET_REFILL", default = 60)
    val userAgent: String = env(name = "API_USER_AGENT")
    val hiscoreCacheTimeMinutes: Int = env(name = "HS_CACHE_TIME_MINUTES", default = 10)
    val geCacheTimeMinutes: Int = env(name = "GE_CACHE_TIME_MINUTES", default = 1)
    val playerCountCacheTimeSeconds: Int = env(name = "PLAYER_COUNT_CACHE_SECONDS", default = 30)
    val volumePath: String = env(name = "API_VOLUME_PATH")
    val worldsListCacheTimeMinutes: Int = env(name = "WORLDS_CACHE_TIME_MINUTES", default = 5)

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