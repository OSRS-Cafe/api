package cafe.osrs.api.clients.worlds

import cafe.osrs.api.utils.BadOptionException

enum class WorldLocation(private val listName: String) {
    UNITED_KINGDOM(listName = "United Kingdom"),
    GERMANY(listName = "Germany"),
    AUSTRALIA(listName = "Australia"),
    UNITED_STATES(listName = "United States");
    companion object {
        fun get(name: String) = getOrNull(name) ?: throw BadOptionException(name, entries.map { it.name })
        fun getOrNull(name: String) = entries.firstOrNull { it.name.equals(other = name, ignoreCase = true) }
        fun fromListName(listName: String) = entries.firstOrNull { it.listName == listName } ?: throw Exception("List name not found: $listName")
    }
}