package org.mgaidamak.cinema.public

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
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
            "/seat/102" -> {
                buildJsonObject {
                    put("id", 102)
                    put("hall", 11)
                    put("x", 2)
                    put("y", 1)
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }

    override fun sessionGet(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/session?hall=10&hall=11" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 1)
                        put("film", 1)
                        put("hall", 11)
                        put("date", "2020-09-10T00:00:00Z")
                        put("price", 100)
                    })
                    add(buildJsonObject {
                        put("id", 2)
                        put("film", 2)
                        put("hall", 11)
                        put("date", "2020-09-10T03:00:00Z")
                        put("price", 200)
                    })
                }.toString() to HttpStatusCode.OK
            }
            "/film/1" -> {
                buildJsonObject {
                    put("id", 1)
                    put("name", "Some like it hot")
                }.toString() to HttpStatusCode.OK
            }
            "/film/2" -> {
                buildJsonObject {
                    put("id", 2)
                    put("name", "Great race")
                }.toString() to HttpStatusCode.OK
            }
            "/session/2" -> {
                buildJsonObject {
                    put("id", 2)
                    put("film", 2)
                    put("hall", 11)
                    put("date", "2020-09-10T03:00:00Z")
                    put("price", 200)
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }

    override fun ticketGet(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/ticket?session=2" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 1)
                        put("bill", 4)
                        put("session", 2)
                        put("seat", 100)
                        put("status", 2)
                    })
                    add(buildJsonObject {
                        put("id", 2)
                        put("bill", 4)
                        put("session", 2)
                        put("seat", 101)
                        put("status", 2)
                    })
                }.toString() to HttpStatusCode.OK
            }
            "/bill/10" -> {
                buildJsonObject {
                    put("id", 10)
                    put("customer", 7)
                    put("session", 2)
                    put("status", 1)
                    put("total", 200)
                }.toString() to HttpStatusCode.OK
            }
            "/ticket?bill=10" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 1)
                        put("bill", 11)
                        put("session", 2)
                        put("seat", 102)
                        put("status", 2)
                    })
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }

    override fun ticketPost(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/bill" -> {
                buildJsonObject {
                    put("id", 10)
                    put("customer", 7)
                    put("session", 2)
                    put("status", 0)
                    put("total", 200)
                }.toString() to HttpStatusCode.OK
            }
            "/ticket" -> {
                buildJsonObject {
                    put("id", 11)
                    put("bill", 10)
                    put("session", 2)
                    put("seat", 102)
                    put("status", 2)
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }

    override fun ticketPatch(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/bill/10" -> {
                buildJsonObject {
                    put("id", 10)
                    put("customer", 7)
                    put("session", 2)
                    put("status", 1)
                    put("total", 200)
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }
}

class SuccessTest {

    /**
     * User looking for cinema at selected city
     */
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
                })
                add(buildJsonObject {
                    put("id", 2)
                    put("name", "Cosmos")
                    put("city", "Novosibirsk")
                    put("address", "Bogdashka")
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    /**
     * User looking for session at selected cinema
     */
    @Test
    fun `get session list`() = testApp {
        handleRequest(HttpMethod.Get, "/public/session?cinema=2").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonArray {
                add(buildJsonObject {
                    put("id", 1)
                    put("film", "Some like it hot")
                    put("hall", 11)
                    put("date", "2020-09-10T00:00:00Z")
                    put("price", 100)
                })
                add(buildJsonObject {
                    put("id", 2)
                    put("film", "Great race")
                    put("hall", 11)
                    put("date", "2020-09-10T03:00:00Z")
                    put("price", 200)
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    /**
     * User looking for free seats (that has 0 status)
     */
    @Test
    fun `get seats`() = testApp {
        handleRequest(HttpMethod.Get, "/public/seat?session=2").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonArray {
                add(buildJsonObject {
                    put("id", 100)
                    put("x", 1)
                    put("y", 1)
                    put("status", 2)
                })
                add(buildJsonObject {
                    put("id", 101)
                    put("x", 1)
                    put("y", 2)
                    put("status", 2)
                })
                add(buildJsonObject {
                    put("id", 102)
                    put("x", 2)
                    put("y", 1)
                    put("status", 0)
                })
                add(buildJsonObject {
                    put("id", 103)
                    put("x", 2)
                    put("y", 2)
                    put("status", 0)
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `post bill`() = testApp {
        handleRequest(HttpMethod.Post, "/public/bill") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(buildJsonObject {
                put("customer", 7)
                put("session", 2)
                put("status", 0)
                put("total", 200)
                put("seats", buildJsonArray {
                    add(buildJsonObject {
                        put("id", 102)
                        put("x", 2)
                        put("y", 1)
                        put("status", 0)
                    })
                })
            }.toString())
        }.apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonObject {
                put("id", 10)
                put("customer", 7)
                put("session", 2)
                put("status", 1)
                put("total", 200)
                put("seats", buildJsonArray {
                    add(buildJsonObject {
                        put("id", 102)
                        put("x", 2)
                        put("y", 1)
                        put("status", 2)
                    })
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `get bill`() = testApp {
        handleRequest(HttpMethod.Get, "/public/bill/10").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonObject {
                put("id", 10)
                put("customer", 7)
                put("session", 2)
                put("status", 1)
                put("total", 200)
                put("seats", buildJsonArray {
                    add(buildJsonObject {
                        put("id", 102)
                        put("x", 2)
                        put("y", 1)
                        put("status", 2)
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