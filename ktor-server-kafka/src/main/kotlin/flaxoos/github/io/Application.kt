package flaxoos.github.io

import flaxoos.github.io.domain.User
import io.github.flaxoos.ktor.server.plugins.kafka.MessageTimestampType
import io.github.flaxoos.ktor.server.plugins.kafka.TopicName
import io.github.flaxoos.ktor.server.plugins.kafka.admin
import io.github.flaxoos.ktor.server.plugins.kafka.common
import io.github.flaxoos.ktor.server.plugins.kafka.components.fromRecord
import io.github.flaxoos.ktor.server.plugins.kafka.components.toRecord
import io.github.flaxoos.ktor.server.plugins.kafka.consumer
import io.github.flaxoos.ktor.server.plugins.kafka.consumerConfig
import io.github.flaxoos.ktor.server.plugins.kafka.consumerRecordHandler
import io.github.flaxoos.ktor.server.plugins.kafka.installKafka
import io.github.flaxoos.ktor.server.plugins.kafka.kafkaAdminClient
import io.github.flaxoos.ktor.server.plugins.kafka.kafkaConsumer
import io.github.flaxoos.ktor.server.plugins.kafka.kafkaConsumerJob
import io.github.flaxoos.ktor.server.plugins.kafka.kafkaProducer
import io.github.flaxoos.ktor.server.plugins.kafka.producer
import io.github.flaxoos.ktor.server.plugins.kafka.registerSchemas
import io.github.flaxoos.ktor.server.plugins.kafka.schemaRegistryClient
import io.github.flaxoos.ktor.server.plugins.kafka.topic
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.call
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureKafka()
    configureSerialization()
    install(checkClients)
}

fun Application.configureRouting() {
    routing {
        route("/welcome") {
            post<User> { user ->
                call.application.log.info("Sending $user to kafka")
                this@configureRouting.sendUser(user)
                call.respondText("Hello ${user.username}")
            }
        }
    }
}

fun Application.configureKafka() {
    installKafka {
        val users = TopicName.named("users")
        schemaRegistryUrl = "http://localhost:8081"
        topic(users) {
            partitions = 1
            replicas = 1
            configs {
                messageTimestampType = MessageTimestampType.CreateTime
            }
        }
        common {
            bootstrapServers = listOf("http://localhost:29092")
            retries = 1
            clientId = "my-client-id"
        }
        admin { }
        producer {
            clientId = "my-client-id"
        }
        consumer {
            groupId = "my-group-id"
            clientId = "my-client-id-override"
        }
        consumerConfig {
            consumerRecordHandler(users) { record ->
                val user = fromRecord<User>(record.value())
                welcomedUsers[user.id] = user
                log.info("Hello user from kafka!: $user")
            }
        }
        registerSchemas {
            User::class at users
        }
    }
}

@Suppress("MagicNumber")
fun Application.sendUser(user: User) {
    this.kafkaProducer?.send(ProducerRecord("users", user.id, user.toRecord()))?.get(100, TimeUnit.MILLISECONDS)
}

val welcomedUsers = ConcurrentHashMap<String, User>()

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

val checkClients = createApplicationPlugin("checkClients") {
    on(MonitoringEvent(ApplicationStarted)) { app ->
        app.kafkaAdminClient.also { app.log.info("Validated admin client") }
        app.kafkaProducer.also { app.log.info("Validated producer") }
        app.kafkaConsumer.also { app.log.info("Validated consumer") }
        app.kafkaConsumerJob.also { app.log.info("Validated consumer job") }
        app.schemaRegistryClient.also { app.log.info("Validated schema registry client") }
    }
}
