package org.mgaidamak.cinema.public.dao

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test Hall part of Public repo with mocked http
 */
private class HallRepo: MockPublicRepo() {
    override fun hallGet(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/cinema?city=Tomsk" -> {
                buildJsonArray { }.toString() to HttpStatusCode.OK
            }
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
            "/seat?hall=10" -> {
                buildJsonArray { }.toString() to HttpStatusCode.OK
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
}

class HallPublicTest {

    private val repo = HallRepo()

    @Test
    fun `get empty cinema list`() {
        val cinemas = runBlocking { repo.getCinemas("Tomsk") }
        assertEquals(HttpStatusCode.OK, cinemas.code)
        assertEquals(emptyList(), cinemas.data)
    }

    @Test
    fun `get cinema list`() {
        val cinemas = runBlocking { repo.getCinemas("Novosibirsk") }
        val expected = listOf(
            AdminCinema(1, "Pobeda", "Novosibirsk", "Lenina", "Asia/Novosibirsk"),
            AdminCinema(2, "Cosmos", "Novosibirsk", "Bogdashka", "Asia/Novosibirsk"))
        assertEquals(HttpStatusCode.OK, cinemas.code)
        assertEquals(expected, cinemas.data)
    }

    @Test
    fun `get empty hall list`() {
        val halls = runBlocking { repo.getHalls(1) }
        assertEquals(HttpStatusCode.OK, halls.code)
        assertEquals(emptyList(), halls.data)
    }

    @Test
    fun `get hall list`() {
        val halls = runBlocking { repo.getHalls(2) }
        val expected = listOf(
            AdminHall(10, 2, "Big"),
            AdminHall(11, 2, "Small")
        )
        assertEquals(HttpStatusCode.OK, halls.code)
        assertEquals(expected, halls.data)
    }

    @Test
    fun `get empty seat list`() {
        val seats = runBlocking { repo.getSeats(10) }
        assertEquals(HttpStatusCode.OK, seats.code)
        assertEquals(emptyList(), seats.data)
    }

    @Test
    fun `get seat list`() {
        val seats = runBlocking { repo.getSeats(11) }
        val expected = listOf(
            AdminSeat(100, 11, 1, 1),
            AdminSeat(101, 11, 1, 2),
            AdminSeat(102, 11, 2, 1),
            AdminSeat(103, 11, 2, 2)
        )
        assertEquals(HttpStatusCode.OK, seats.code)
        assertEquals(expected, seats.data)
    }

    @Test
    fun `get seat`() {
        val seat = runBlocking { repo.getSeat(102) }
        val expected = AdminSeat(102, 11, 2, 1)
        assertEquals(HttpStatusCode.OK, seat.code)
        assertEquals(expected, seat.data)
    }

    @Test
    fun `get wrong seat`() {
        val r = runBlocking { repo.getSeat(202) }
        assertEquals(HttpStatusCode.NotFound, r.code)
    }

    // TODO test other Http codes!
}