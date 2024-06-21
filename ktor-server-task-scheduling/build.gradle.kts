plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version libs.versions.ktor
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation("io.github.flaxoos:ktor-server-task-scheduling:${project.property("version") as String}")
    testImplementation(libs.testcontainers)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.contentNegotiation)
    testImplementation(libs.exposed.core)
    testImplementation(libs.exposed.jdbc)
    testImplementation(libs.exposed.dao)
    testImplementation(libs.exposed.kotlin.datetime)
    testImplementation(libs.mongodb.driver.kotlin.coroutine)
    testImplementation(libs.kotest.extensions.testcontainers)
    testImplementation(libs.testcontainers.redis)
    testImplementation(libs.testcontainers.postgres)
    testImplementation(libs.testcontainers.mongodb)
    testRuntimeOnly(libs.postgresql)
}

application {
    mainClass.set("io.github.flaxoos.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}
