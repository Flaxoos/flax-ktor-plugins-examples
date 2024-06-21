pluginManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "flax-ktor-plugins-examples"
include("ktor-server-kafka")
include("ktor-server-rate-limiting")
include("ktor-server-task-scheduling")
