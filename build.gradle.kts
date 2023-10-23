import java.net.URI

plugins {
    idea
}
subprojects {
    repositories {
        mavenCentral()
//        maven {
//            url = URI("https://s01.oss.sonatype.org/service/local/repositories/iogithubflaxoos-1000/content")
//            credentials {
//                username = project.findProperty("ossrh.username")!!.toString()
//                password = project.findProperty("ossrh.password")!!.toString()
//            }
//        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
