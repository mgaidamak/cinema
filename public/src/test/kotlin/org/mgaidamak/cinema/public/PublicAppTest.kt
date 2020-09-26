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
import org.mgaidamak.cinema.public.dao.Bill
import org.mgaidamak.cinema.public.dao.Cinema
import org.mgaidamak.cinema.public.dao.Seat
import org.mgaidamak.cinema.public.dao.Session
import org.mgaidamak.cinema.public.response.Response
import org.mgaidamak.cinema.public.route.IPublicRoute
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

private class SuccessRoute: IPublicRoute {
    override suspend fun getCinemas(city: String?): Response<Collection<Cinema>> {
        return when(city) {
            "Novosibirsk" -> Response(data = listOf(
                Cinema(1, "Pobeda", "Novosibirsk", "Lenina"),
                Cinema(2, "Cosmos", "Novosibirsk", "Bogdashka")
            ))
            else -> Response(data = emptyList())
        }
    }

    override suspend fun getSessions(cinema: Int, date: LocalDate): Response<Collection<Session>> {
        return when(cinema) {
            2 -> Response(data = listOf(
                Session(1, "Some like it hot", 11, "2020-09-10T00:00:00Z", 100),
                Session(2, "Great race", 11, "2020-09-10T03:00:00Z", 200),
            ))
            else -> Response(data = emptyList())
        }
    }

    override suspend fun getSeats(session: Int): Response<Collection<Seat>> {
        return when(session) {
            2 -> Response(data = listOf(
                Seat(100, 1, 1, 2),
                Seat(101, 1, 2, 2),
                Seat(102, 2, 1, 0),
                Seat(103, 2, 2, 0)
            ))
            else -> Response(data = emptyList())
        }
    }

    override suspend fun postBill(bill: Bill) =
        Response(data = Bill(10, 7, 2, 1, 200,
            listOf(Seat(102, 2, 1, 2))))

    override suspend fun getBill(id: Int): Response<Bill> {
        return when(id) {
            10 -> Response(data = Bill(10, 7, 2, 1, 200,
                listOf(Seat(102, 2, 1, 2))))
            else -> Response(HttpStatusCode.NotFound, "Not found by $id")
        }
    }

    override suspend fun deleteBill(id: Int): Response<Bill> {
        return when(id) {
            10 -> Response(data = Bill(10, 7, 2, 3, 200,
                listOf(Seat(102, 2, 1, 2))))
            else -> Response(HttpStatusCode.NotFound, "Not found by $id")
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

    @Test
    fun `delete bill`() = testApp {
        handleRequest(HttpMethod.Delete, "/public/bill/10").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonObject {
                put("id", 10)
                put("customer", 7)
                put("session", 2)
                put("status", 3)
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
            public(SuccessRoute())
        }, callback)
    }
}