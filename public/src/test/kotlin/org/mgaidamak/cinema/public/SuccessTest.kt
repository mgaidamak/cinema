package org.mgaidamak.cinema.public

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.mgaidamak.cinema.public.dao.MockPublicRepo
import kotlin.test.Test
import kotlin.test.assertEquals

private class SuccessRepo: MockPublicRepo() {
    override fun hallGet(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/cinema?city=Novosibirsk" -> {
                buildJsonArray {
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
                }.toString() to HttpStatusCode.OK
            }
            "/hall?cinema=1" -> {
                buildJsonArray { }.toString() to HttpStatusCode.OK
            }
            "/hall?cinema=2" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 10)
                        put("cinema", 2)
                        put("name", "Big")
                    })
                    add(buildJsonObject {
                        put("id", 11)
                        put("cinema", 2)
                        put("name", "Small")
                    })
                }.toString() to HttpStatusCode.OK
            }
            "/seat?hall=11" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 100)
                        put("hall", 11)
                        put("x", 1)
                        put("y", 1)
                    })
                    add(buildJsonObject {
                        put("id", 101)
                        put("hall", 11)
                        put("x", 1)
                        put("y", 2)
                    })
                    add(buildJsonObject {
                        put("id", 102)
                        put("hall", 11)
                        put("x", 2)
                        put("y", 1)
                    })
                    add(buildJsonObject {
                        put("id", 103)
                        put("hall", 11)
                        put("x", 2)
                        put("y", 2)
                    })
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }
}

class SuccessTest {

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
                    put("halls", buildJsonArray { })
                })
                add(buildJsonObject {
                    put("id", 2)
                    put("name", "Cosmos")
                    put("city", "Novosibirsk")
                    put("address", "Bogdashka")
                    put("halls", buildJsonArray {
                        add(buildJsonObject {
                            put("id", 10)
                            put("name", "Big")
                        })
                        add(buildJsonObject {
                            put("id", 11)
                            put("name", "Small")
                        })
                    })
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            public(SuccessRepo())
        }, callback)
    }
}