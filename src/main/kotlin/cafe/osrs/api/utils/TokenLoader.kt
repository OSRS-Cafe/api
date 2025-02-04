package cafe.osrs.api.utils

data class APIToken(
    val token: String,
    val weight: Int
)

//TODO: Find a way to implement this. Currently this is only a placeholder.
object TokenLoader {
    fun getToken(token: String?): APIToken? = null
}