package io.github.flaxoos

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.flaxoos.ktor.server.plugins.taskscheduling.TaskScheduling
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.jdbc
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.mongoDb
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.redis.redis
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.AttributeKey
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>): Unit =
    io.ktor.server.cio.EngineMain
        .main(args)

val attributeKey = AttributeKey<MutableList<String>>("task-outputs")

fun Application.module() {
    attributes.put(attributeKey, mutableListOf())
    install(TaskScheduling) {
        val redisManagerName = "redis"
        val jdbcManagerName = "jdbc"
        val mongoDbManagerName = "mongo"

        with(this@module.environment.config) {
            redis(redisManagerName) {
                host = property("redis.host").getString()
                port = property("redis.port").getString().toInt()
                username = "flaxoos"
                password = "password"
                connectionAcquisitionTimeoutMs = 1000
                lockExpirationMs = 4500
            }
            jdbc(jdbcManagerName) {
                database =
                    Database.connect(
                        url = property("postgres.jdbcUrl").getString(),
                        driver = "org.postgresql.Driver",
                        user = property("postgres.username").getString(),
                        password = property("postgres.password").getString(),
                    )
            }
            mongoDb(mongoDbManagerName) {
                client = MongoClient.create(connectionString = property("mongodb.connectionString").getString())
                databaseName = "flaxoos"
            }
        }
        task(redisManagerName) {
            name = "redis-managed-task"
            concurrency = 1
            task = {
                attributes[attributeKey].add(this@task.name)
            }
            kronSchedule = {
                seconds {
                    0 every 3
                }
            }
        }
        task(jdbcManagerName) {
            name = "jdbc-managed-task"
            concurrency = 1
            task = {
                attributes[attributeKey].add(this@task.name)
            }
            kronSchedule = {
                seconds {
                    1 every 3
                }
            }
        }
        task(mongoDbManagerName) {
            name = "mongo-managed-task"
            concurrency = 1
            task = {
                attributes[attributeKey].add(this@task.name)
            }
            kronSchedule = {
                seconds {
                    2 every 3
                }
            }
        }
    }
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("tasks") {
            call.respond(this@module.attributes[attributeKey])
        }
    }
}
