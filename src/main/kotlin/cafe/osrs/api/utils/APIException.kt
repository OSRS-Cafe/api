package cafe.osrs.api.utils

import io.ktor.http.*

open class RSApiException(
    override val message: String, //Override so that its never null
    val status: HttpStatusCode
): Exception(message)

class NameTooLongException(name: String): RSApiException("Player name ($name) cant exceed 12 characters", HttpStatusCode.BadRequest)
class NameEmptyException: RSApiException("Player name cant be empty", HttpStatusCode.BadRequest)