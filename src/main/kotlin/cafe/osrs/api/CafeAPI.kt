package cafe.osrs.api

import cafe.osrs.api.routes.PlayerRoute
import cafe.osrs.api.routes.RankRoute
import cafe.osrs.api.utils.RSApiException
import cafe.osrs.api.utils.TokenLoader
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

object CafeAPI {
    init {
        embeddedServer(
            factory = Netty,
            port = APIConfig.port,
            host = APIConfig.host,
            module = setup()
        ).start(wait = true)
    }

    private fun setup(): Application.() -> Unit = {
        setupRateLimit()()
        setupCors()()
        setupContentNegotiation()()
        setupRoutes()()
        setupStatusPages()()
    }

    private fun setupStatusPages(): Application.() -> Unit = {
        install(StatusPages) {
            exception<RSApiException> { call, cause ->
                call.respondText(status = cause.status, text = cause.message)
            }
        }
    }

    private fun setupCors(): Application.() -> Unit = {
        install(CORS) {
            anyHost()
        }
    }

    private fun setupRateLimit(): Application.() -> Unit = {
        install(RateLimit) {
            global {
                rateLimiter(limit = APIConfig.tokenBucket, refillPeriod = APIConfig.tokenBucketRefill.seconds)
                requestKey { call ->
                    val tokenHeader = call.request.header("rs-api-token")
                    TokenLoader.getToken(tokenHeader)?.token ?: call.request.host()
                }
                requestWeight { call, key ->
                    val foundToken = TokenLoader.getToken(key as String)
                    val weight = foundToken?.weight ?: APIConfig.tokenWeight
                    call.response.header("X-RateLimit-Valid-API-Key", if (foundToken == null) "false" else "true")
                    call.response.header("X-RateLimit-Weight", weight)
                    return@requestWeight weight
                }
            }
        }
    }

    private fun setupContentNegotiation(): Application.() -> Unit = {
        install(ContentNegotiation) {
            json(Json)
        }
    }

    private fun setupRoutes(): Application.() -> Unit = {
        routing {
            PlayerRoute()
            RankRoute()
        }
    }
}