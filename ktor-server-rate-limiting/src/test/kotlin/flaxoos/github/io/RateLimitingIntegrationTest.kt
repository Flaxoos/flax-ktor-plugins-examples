package flaxoos.github.io

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.userAgent
import io.ktor.server.testing.testApplication


class RateLimitingIntegrationTest : FunSpec() {

    init {
        test("Limiting should apply") {
            testApplication {
                (1..20).map { i ->
                    withClue("request $i") {
                        client.get("welcome") {
                            this.headers {
                                userAgent("test")
                            }
                        }.status.also { println("Status: $it") } shouldBe if (i <= 10) {
                            HttpStatusCode.OK
                        } else {
                            HttpStatusCode.TooManyRequests
                        }
                    }
                }
            }
        }
    }
}
