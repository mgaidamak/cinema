package org.mgaidamak

import io.ktor.http.*
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.cinema.DbCinemaRepo
import org.mgaidamak.dao.cinema.DbCinemaRepoTest
import org.mgaidamak.dao.cinema.FileCinemaRepo
import org.mgaidamak.dao.hall.DbHallRepo
import org.mgaidamak.dao.hall.FileHallRepo
import org.mgaidamak.dao.seat.DbSeatRepo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HallAppTest {
    @Test
    fun `test root`() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertEquals("My Hall App", response.content)
        }
    }

    @BeforeTest
    fun `clean up`() {
        crepo.clear()
    }

    @Test
    fun `create`() = testApp {
        handleRequest(HttpMethod.Post, "/cinema") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(buildJsonObject {
                put("name", "Pobeda")
                put("city", "Novosibirsk")
                put("address", "Lenina 1")
                put("timezone", "Asia/Novosibirsk")
            }.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", crepo.next.get())
                put("name", "Pobeda")
                put("city", "Novosibirsk")
                put("address", "Lenina 1")
                put("timezone", "Asia/Novosibirsk")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `get one`() = testApp {
        val id = assertNotNull(crepo.createCinema(Cinema(2, "Pobeda", "Novosibirsk",
            "Lenina 1", "Asia/Novosibirsk")))?.id

        handleRequest(HttpMethod.Get, "/cinema/$id").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", id)
                put("name", "Pobeda")
                put("city", "Novosibirsk")
                put("address", "Lenina 1")
                put("timezone", "Asia/Novosibirsk")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `get absent`() = testApp {
        crepo.map[2] = Cinema(3, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

        handleRequest(HttpMethod.Get, "/cinema/33").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found by 33", response.content)
        }
    }

    @Test
    fun `get invalid id`() = testApp {
        handleRequest(HttpMethod.Get, "/cinema/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Invalid id", response.content)
        }
    }

    @Test
    fun `list all`() = testApp {
        crepo.map[4] = Cinema(4, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")
        crepo.map[5] = Cinema(5, "Cosmos", "Moscow", "Petrovka 38", "Europe/Moscow")

        handleRequest(HttpMethod.Get, "/cinema").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonArray {
                add(buildJsonObject {
                    put("id", 4)
                    put("name", "Pobeda")
                    put("city", "Novosibirsk")
                    put("address", "Lenina 1")
                    put("timezone", "Asia/Novosibirsk")
                })
                add(buildJsonObject {
                    put("id", 5)
                    put("name", "Cosmos")
                    put("city", "Moscow")
                    put("address", "Petrovka 38")
                    put("timezone", "Europe/Moscow")
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `delete one`() = testApp {
        crepo.map[6] = Cinema(6, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

        handleRequest(HttpMethod.Delete, "/cinema/6").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", 6)
                put("name", "Pobeda")
                put("city", "Novosibirsk")
                put("address", "Lenina 1")
                put("timezone", "Asia/Novosibirsk")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `delete absent`() = testApp {
        crepo.map[6] = Cinema(6, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

        handleRequest(HttpMethod.Delete, "/cinema/33").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found by 33", response.content)
        }
    }

    @Test
    fun `delete invalid id`() = testApp {
        handleRequest(HttpMethod.Delete, "/cinema/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Invalid id", response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            cinema(crepo)
            hall(hrepo)
            seat(srepo)
        }, callback)
    }

    companion object {
        val crepo = FileCinemaRepo()
        val hrepo = FileHallRepo()
        val srepo = DbSeatRepo(DbCinemaRepoTest.url)
    }
}