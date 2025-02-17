import org.ajoberstar.grgit.Grgit
import org.gradle.internal.os.OperatingSystem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "3.0.2"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.ajoberstar.grgit") version "4.1.1" //Used to determine the status of the repo
}

group = "cafe.osrs"
version = v("api")

repositories {
    mavenCentral()
}

dependencies {
    multiImplementation(group = "io.ktor", version = v("ktor")) {
        //ktor server
        add("ktor-server-core")
        add("ktor-server-netty")
        add("ktor-server-content-negotiation")
        add("ktor-server-cors")
        add("ktor-server-rate-limit")
        add("ktor-server-status-pages")
        add("ktor-server-websockets")
        //ktor client
        add("ktor-client-core")
        add("ktor-client-cio")
        add("ktor-client-content-negotiation")
        //ktor common
        add("ktor-serialization-kotlinx-json")
    }

    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = v("serialization"))

    implementation(group = "ch.qos.logback", name = "logback-classic", version = v("logback"))
}

application {
    mainClass.set("cafe.osrs.api.MainKt")
}

//We only want a fat jar
tasks.named("jar") {
    enabled = false
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from("src/main/resources/") {
        include("buildinfo.json")
        val javaToolchain = project.extensions.findByType(JavaPluginExtension::class.java)?.toolchain
        val buildDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))

        val gitInfo = if(isGitProject()) {
            GitInfo(
                hash = grgit.head().id,
                branch = grgit.branch.current().name,
                dirty = !grgit.status().isClean
            )
        } else if(!System.getenv("RAILWAY_PROJECT_NAME").isNullOrBlank()) {
            //Railway does not clone .git
            GitInfo(
                hash = System.getenv("RAILWAY_GIT_COMMIT_SHA"),
                branch = System.getenv("RAILWAY_GIT_BRANCH"),
                dirty = false
            )
        } else {
            GitInfo("unknown", "unknown", false)
        }

        expand (
            "version" to version,
            "buildDate" to "$buildDate (${TimeZone.getDefault().id})",
            "githash" to gitInfo.hash.substring(0, 7),
            "githashFull" to gitInfo.hash,
            "branch" to gitInfo.branch,
            "dirty" to gitInfo.dirty,
            "osname" to System.getProperty("os.name"),
            "osversion" to getSystemVersion(),
            "osarch" to System.getProperty("os.arch"),
            "java" to (javaToolchain?.languageVersion?.orNull ?: "null")
        )
    }
    outputs.upToDateWhen { false } // Forces execution every time
}

kotlin {
    jvmToolchain(17)
}

data class GitInfo(
    val hash: String,
    val branch: String,
    val dirty: Boolean
)

fun isGitProject(): Boolean {
    return try {
        grgit.head()
        true
    } catch (e: Exception) {
        false
    }
}

fun getSystemVersion(): String {
    if(!OperatingSystem.current().isWindows) return System.getProperty("os.version")
    return Runtime.getRuntime()
        .exec(arrayOf("cmd.exe", "/c", "ver"))
        .inputReader()
        .readLines()
        .joinToString(separator = "") { it.ifBlank { "" } }
}

//Utils for pretty and easy dependency adding
class MultiImplementationScope(
    private val scope: DependencyHandlerScope,
    private val group: String,
    private val version: String
) {
    fun add(name: String) = scope.implementation(group = group, name = name, version = version)
}

fun DependencyHandlerScope.multiImplementation(group: String, version: String, scope: MultiImplementationScope.() -> Unit) {
    scope.invoke(MultiImplementationScope(scope = this, group = group, version = version))
}
fun v(name: String) = project.property("version.$name") as String