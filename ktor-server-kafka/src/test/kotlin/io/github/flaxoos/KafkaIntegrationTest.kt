package io.github.flaxoos

import io.github.flaxoos.domain.User
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.delay
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait.forHttp
import org.testcontainers.containers.wait.strategy.Wait.forListeningPort
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class KafkaIntegrationTest : FunSpec() {
    companion object {
        private val waitTimeout = 30.seconds.toJavaDuration()
        val kafka: DockerComposeContainer<*> =
            DockerComposeContainer(
                "kafka",
                listOf(File(Companion::class.java.getResource("/docker-compose.yaml")!!.file))
            )
                .withExposedService("zookeeper", 2181)
                .withExposedService("broker", 29092)
                .withExposedService("schema-registry", 8081)
                .waitingFor("zookeeper", forListeningPort().withStartupTimeout(waitTimeout))
                .waitingFor("broker", forListeningPort().withStartupTimeout(waitTimeout))
                .waitingFor("schema-registry", forHttp("/subjects").withStartupTimeout(waitTimeout))
    }

    init {
        beforeSpec {
            kafka.start()
        }

        afterSpec {
            kafka.stop()
        }

        test("Kafka should be setup with a producer and consumer") {
            testApplication {
                val client = createClient {
                    install(ContentNegotiation) {
                        json()
                    }
                }
                val id = "test-kafka-user-id"
                val user = User(id, "test-kafka-username")
                val response = client.post("welcome") {
                    contentType(ContentType.Application.Json)
                    setBody(user)
                }

                response.status shouldBe HttpStatusCode.OK

                delay(1000.milliseconds)

                welcomedUsers[id] shouldBe User(id, "test-kafka-username")
            }
        }
    }
}
