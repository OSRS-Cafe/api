package cafe.osrs.api.utils

import cafe.osrs.api.clients.hiscore.HiscoreMode
import io.ktor.http.*

open class RSApiException(
    override val message: String, //Override so that its never null
    val status: HttpStatusCode
): Exception(message)

class BadHiscoreModeException : RSApiException("Bad Mode Format! Available: ${HiscoreMode.prettyList}", HttpStatusCode.BadRequest)
class NameTooLongException(name: String): RSApiException("Player name ($name) cant exceed 12 characters", HttpStatusCode.BadRequest)
class NameEmptyException: RSApiException("Player name cant be empty", HttpStatusCode.BadRequest)