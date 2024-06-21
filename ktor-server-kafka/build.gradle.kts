plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version libs.versions.ktor
    alias(libs.plugins.kotlin.serialization) apply false
    application
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.cio.jvm)
    implementation(libs.ktor.server.contentNegotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
    implementation("io.github.flaxoos:ktor-server-kafka:${project.property("version") as String}")
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.testcontainers)
    testImplementation(libs.ktor.client.contentNegotiation)
}

application {
    mainClass.set("io.github.flaxoos.ApplicationKt")
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
