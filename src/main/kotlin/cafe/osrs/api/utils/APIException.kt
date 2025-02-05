package cafe.osrs.api.utils

import cafe.osrs.api.clients.hiscore.HiscoreMode
import io.ktor.http.*

open class RSApiException(
    override val message: String, //Override so that its never null
    val status: HttpStatusCode
): Exception(message)

class GenericRSApiException(message: String = "No info provided"): RSApiException(message = "Ouch! We have encountered an issue while working on your request. Info: $message.", status = HttpStatusCode.InternalServerError)
class BadHiscoreModeException : RSApiException(message = "Bad Mode Format! Available: ${HiscoreMode.prettyList}", status = HttpStatusCode.BadRequest)
class NameTooLongException(name: String): RSApiException(message = "Player name ($name) cant exceed 12 characters", status = HttpStatusCode.BadRequest)
class NameEmptyException: RSApiException(message = "Player name cant be empty", status = HttpStatusCode.BadRequest)