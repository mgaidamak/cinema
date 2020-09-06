package org.mgaidamak.cinema.public

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondOk
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.mgaidamak.cinema.public.dao.IPublicRepo
import kotlin.test.Test
import kotlin.test.assertEquals

@KtorExperimentalAPI
private class MockPublicRepo: IPublicRepo() {

    override val hallClient: HttpClient
        get() = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        "/cinema" -> {
                            val list = """
                                [{"id":1,"name":"Pobeda","city":"Novosibirsk","address":"Lenina","timezone":"Asia/Novosibirsk"},
                                {"id":2,"name":"Cosmos","city":"Novosibirsk","address":"Bogdashka","timezone":"Asia/Novosibirsk"}]
                            """.trimIndent()
                            respondOk(list)
                        }
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }

    override val sessionClient: HttpClient
        get() = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }

    override val ticketClient: HttpClient
        get() = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }
}

class PublicAppTest {

    @Test
    fun `get cinema list`() = testApp {
        handleRequest(HttpMethod.Get, "/public/cinema?city=Novosibirsk").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonArray {
                add(buildJsonObject {
                    put("id", 1)
                    put("name", "Pobeda")
                    put("city", "Novosibirsk")
                    put("address", "Lenina")
                    put("timezone", "Asia/Novosibirsk")
                })
                add(buildJsonObject {
                    put("id", 2)
                    put("name", "Cosmos")
                    put("city", "Novosibirsk")
                    put("address", "Bogdashka")
                    put("timezone", "Asia/Novosibirsk")
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            public(MockPublicRepo())
        }, callback)
    }
}