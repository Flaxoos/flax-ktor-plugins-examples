plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version libs.versions.ktor
    alias(libs.plugins.kotlin.serialization)
    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
    implementation("io.github.flaxoos:ktor-server-rate-limiting:${project.property("version") as String}")
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.contentNegotiation)
}

application {
    mainClass.set("io.github.flaxoos.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}
