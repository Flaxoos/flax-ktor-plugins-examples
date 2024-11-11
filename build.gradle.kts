plugins {
    idea
    kotlin("multiplatform") version "2.0.20" apply false
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
    }
}
