plugins {
    kotlin("jvm") version libs.versions.kotlin
    id("io.ktor.plugin") version libs.versions.ktor
    alias(libs.plugins.kotlin.serialization)
    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.cio.jvm)
    implementation(libs.ktor.server.contentNegotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
    implementation("io.github.flaxoos:ktor-server-kafka:1.2.6")
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.testcontainers)
    testImplementation(libs.ktor.client.contentNegotiation)
}

application {
    mainClass.set("flaxoos.github.io.ApplicationKt")
}

val composeUp by tasks.registering {
    doFirst {
        exec {
            commandLine("docker-compose", "up", "-d")
        }
    }
}

val composeDown by tasks.registering {
    doFirst {
        exec {
            commandLine("docker-compose", "down", "-v")
        }
    }
}

tasks.named("run") {
    dependsOn(composeUp)
    finalizedBy(composeDown)
}

tasks.test {
    useJUnitPlatform()
}
