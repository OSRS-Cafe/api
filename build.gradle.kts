plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "3.0.2"
    kotlin("plugin.serialization") version "2.1.0"
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
    implementation(group = "io.swagger.codegen.v3", name = "swagger-codegen-generators", version = "+")
}

application {
    mainClass.set("cafe.osrs.api.MainKt")
}

tasks.named("jar") {
    enabled = false
}

kotlin {
    jvmToolchain(17)
}

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