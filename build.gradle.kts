import java.net.URI

plugins {
    idea
}
subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url =
                URI(
                    "https://maven.pkg.github.com/flaxoos/${project.name}"
                )
            credentials {
                username = gprUser
                password = gprReadToken
            }
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

private val Project.gprUser
    get() = findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")

private val Project.gprReadToken
    get() = findProperty("gpr.read.key") as String? ?: System.getenv("GPR_READ_TOKEN")
