pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "flax-ktor-plugins-examples"
include("ktor-server-kafka")
include("ktor-server-rate-limiting")