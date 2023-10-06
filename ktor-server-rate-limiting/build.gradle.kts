plugins {
    kotlin("jvm") version libs.versions.kotlin
    id("io.ktor.plugin") version libs.versions.ktor
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
    implementation("io.github.flaxoos:ktor-server-rate-limiting:1.2.3")
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.contentNegotiation)
}

application {
    mainClass.set("flaxoos.github.io.ApplicationKt")
}

val Project.gprReadToken: String?
    get() = findProperty("gpr.read.key") as String? ?: System.getenv("GPR_READ_TOKEN")

val Project.gprUser: String?
    get() = findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
