plugins {
    idea
    kotlin("multiplatform") version "1.9.20" apply false
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url =uri( "https://packages.confluent.io/maven/")
        }
    }
}
