import io.github.flaxoos.ktor.versionOf
import kotlinx.css.i
import java.net.URI

plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version libs.versions.ktor
    alias(libs.plugins.kotlin.serialization)
    application
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    maven {
        name = "GitHubPackages"
        url =
            URI("https://maven.pkg.github.com/flaxoos/flax-ktor-plugins")
        credentials {
            username = gprUser
            password = gprReadToken
        }
    }
}
dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)
    implementation("io.github.flaxoos:ktor-server-kafka:${property("VERSION")}")
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

val Project.gprReadToken: String?
    get() = findProperty("gpr.read.key") as String? ?: System.getenv("GPR_READ_TOKEN")

val Project.gprUser: String?
    get() = findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")