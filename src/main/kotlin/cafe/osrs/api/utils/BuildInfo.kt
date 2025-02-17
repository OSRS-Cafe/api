package cafe.osrs.api.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BuildInfoDTO(
    val version: String,
    val buildDate: String,
    val githash: String,
    val githashFull: String,
    val branch: String,
    val dirty: Boolean,
    val build: SystemInfo
) {
    val system = SystemInfo(
        osname = System.getProperty("os.name"),
        osversion = getSystemVersion(),
        osarch = System.getProperty("os.arch"),
        java = Runtime.version().toString()
    )
    val fullVersion: String = "$version-$branch($githash)${if(dirty) "+dirty" else "" }"

    @Serializable
    data class SystemInfo(
        val osname: String,
        val osversion: String,
        val osarch: String,
        val java: String
    )
}

//TODO: Add system specs like ram/cpu?
object BuildInfo {
    val info = Json.decodeFromString<BuildInfoDTO>(BuildInfo::class.java.getResource("/buildinfo.json")?.readText() ?: throw Exception("buildinfo.json could not be loaded!"))
}

fun getSystemVersion(): String {
    if(!System.getProperty("os.name").contains("windows", true)) return System.getProperty("os.version")
    return Runtime.getRuntime()
        .exec(arrayOf("cmd.exe", "/c", "ver"))
        .inputReader()
        .readLines()
        .joinToString(separator = "") { it.ifBlank { "" } }
}