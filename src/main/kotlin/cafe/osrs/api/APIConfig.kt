package cafe.osrs.api

object APIConfig {
    val host = System.getenv("API-HOST")
    val port = System.getenv("API-PORT").toInt()
    val tokenWeight = System.getenv("API-TOKEN-WEIGHT").toInt()
    val tokenBucket = System.getenv("API-TOKEN-BUCKET").toInt()
    val tokenBucketRefill = System.getenv("API-TOKEN-BUCKET-REFILL").toInt()
    val userAgent = System.getenv("API-USER-AGENT")
}