plugins {
    kotlin("jvm") version "1.9.22"
}

group = "cafe.osrs"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}