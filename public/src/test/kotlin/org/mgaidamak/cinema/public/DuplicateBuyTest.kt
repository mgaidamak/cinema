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
import kotlin.test.assertEquals

private class DuplicateBuyRepo: MockPublicRepo() {
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
                "" to HttpStatusCode.Conflict
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }
}

class DuplicateBuyTest {

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
                        put("id", 100)
                        put("x", 1)
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

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            public(DuplicateBuyRepo())
        }, callback)
    }
}