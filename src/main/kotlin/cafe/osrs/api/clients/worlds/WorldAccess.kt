package cafe.osrs.api.clients.worlds

import cafe.osrs.api.utils.BadOptionException

enum class WorldAccess(private val listName: String) {
    FREE("Free"),
    MEMBERS("Members");
    companion object {
        fun get(name: String) = WorldAccess.getOrNull(name) ?: throw BadOptionException(name, entries.map { it.name })
        fun getOrNull(name: String) = entries.firstOrNull { it.name.equals(other = name, ignoreCase = true) }
        fun fromListName(listName: String) = entries.firstOrNull { it.listName == listName } ?: throw Exception("List name not found: $listName")
    }
}