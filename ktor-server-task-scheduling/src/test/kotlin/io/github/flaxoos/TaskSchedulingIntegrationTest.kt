package io.github.flaxoos

import com.redis.testcontainers.RedisContainer
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.ContainerExtension
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.config.mergeWith
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.delay
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class TaskSchedulingIntegrationTest : FunSpec() {
    private val mongodb = MongoDBContainer(DockerImageName.parse("mongo:6.0.4"))
    private val mongodbContainer =
        install(ContainerExtension(mongodb)) {
            waitingFor(Wait.forListeningPort())
        }
    private val redis = RedisContainer(DockerImageName.parse("redis:latest"))
    private val redisContainer =
        install(ContainerExtension(redis)) {
            waitingFor(Wait.forListeningPort()).withStartupTimeout(1.minutes.toJavaDuration())
        }
    private val postgres = PostgreSQLContainer(DockerImageName.parse("postgres:13.3"))
    private val postgresContainer =
        install(ContainerExtension(postgres)) {
            waitingFor(Wait.forListeningPort())
        }

    init {
        test("Task should execute") {
            testApplication {
                application {
                    module()
                }
                environment {
                    config =
                        config.mergeWith(
                            MapApplicationConfig(
                                listOf(
                                    "mongodb.connectionString" to mongodbContainer.connectionString,
                                    "postgres.jdbcUrl" to postgresContainer.jdbcUrl,
                                    "postgres.username" to postgresContainer.username,
                                    "postgres.password" to postgresContainer.password,
                                    "redis.host" to redisContainer.host,
                                    "redis.port" to redisContainer.firstMappedPort.toString(),
                                    "redis.username" to "flaxoos",
                                    "redis.password" to "password",
                                ),
                            ),
                        )
                }
                startApplication()

                val client =
                    createClient {
                        install(ContentNegotiation) {
                            json()
                        }
                    }

                delay(3500)

                val response = client.get("tasks")

                response.status shouldBe HttpStatusCode.OK
                val tasks: List<String> = response.body()
                kotlin.io.path.createTempFile()
                tasks shouldContainAll listOf("redis-managed-task", "jdbc-managed-task", "mongo-managed-task")
            }
        }
    }
}
