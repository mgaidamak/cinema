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
import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.cinema.FileCinemaRepo
import org.mgaidamak.dao.hall.FileHallRepo
import org.mgaidamak.dao.seat.DbSeatRepo
import org.mgaidamak.dao.seat.FileSeatRepo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
        hrepo.clear()
        srepo.clear()
    }

    @Test
    fun `create cinema`() = testApp {
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
    fun `get cinema`() = testApp {
        crepo.map[3] = Cinema(3, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")
        handleRequest(HttpMethod.Get, "/cinema/3").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", 3)
                put("name", "Pobeda")
                put("city", "Novosibirsk")
                put("address", "Lenina 1")
                put("timezone", "Asia/Novosibirsk")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `get cinema absent`() = testApp {
        crepo.map[2] = Cinema(3, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

        handleRequest(HttpMethod.Get, "/cinema/33").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found by 33", response.content)
        }
    }

    @Test
    fun `get cinema invalid id`() = testApp {
        handleRequest(HttpMethod.Get, "/cinema/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Invalid id", response.content)
        }
    }

    @Test
    fun `list cinema`() = testApp {
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
    fun `delete cinema`() = testApp {
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
    fun `delete cinema absent`() = testApp {
        crepo.map[6] = Cinema(6, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

        handleRequest(HttpMethod.Delete, "/cinema/33").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found by 33", response.content)
        }
    }

    @Test
    fun `delete cinema invalid id`() = testApp {
        handleRequest(HttpMethod.Delete, "/cinema/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Invalid id", response.content)
        }
    }

    @Test
    fun `create hall`() = testApp {
        crepo.map[6] = Cinema(6, "Pobeda", "Novosibirsk",
            "Lenina 1", "Asia/Novosibirsk")
        handleRequest(HttpMethod.Post, "/hall") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(buildJsonObject {
                put("cinema", 6)
                put("name", "Big")
            }.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", hrepo.next.get())
                put("cinema", 6)
                put("name", "Big")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `get hall`() = testApp {
        hrepo.map[4] = Hall(4, 3, "Big")
        handleRequest(HttpMethod.Get, "/hall/4").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", 4)
                put("cinema", 3)
                put("name", "Big")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `get hall absent`() = testApp {
        hrepo.map[4] = Hall(4, 3, "Big")
        handleRequest(HttpMethod.Get, "/hall/44").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found by 44", response.content)
        }
    }

    @Test
    fun `get hall invalid id`() = testApp {
        handleRequest(HttpMethod.Get, "/hall/invalid").apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
            assertEquals("Invalid id", response.content)
        }
    }

    @Test
    fun `list hall`() = testApp {
        hrepo.map[4] = Hall(4, 3, "Big")
        hrepo.map[5] = Hall(5, 3, "Small")
        hrepo.map[6] = Hall(6, 4, "Small")
        handleRequest(HttpMethod.Get, "/hall?cinema=3").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonArray {
                add(buildJsonObject {
                    put("id", 4)
                    put("cinema", 3)
                    put("name", "Big")
                })
                add(buildJsonObject {
                    put("id", 5)
                    put("cinema", 3)
                    put("name", "Small")
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `list hall no cinema`() = testApp {
        hrepo.map[4] = Hall(4, 3, "Big")
        hrepo.map[5] = Hall(5, 3, "Small")
        handleRequest(HttpMethod.Get, "/hall").apply {
            assertEquals(400, response.status()?.value)
        }
    }

    @Test
    fun `delete hall`() = testApp {
        hrepo.map[4] = Hall(4, 3, "Big")
        handleRequest(HttpMethod.Delete, "/hall/4").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", 4)
                put("cinema", 3)
                put("name", "Big")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `delete hall absent`() = testApp {
        hrepo.map[4] = Hall(4, 3, "Big")
        handleRequest(HttpMethod.Delete, "/hall/44").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found by 44", response.content)
        }
    }

    @Test
    fun `delete hall invalid id`() = testApp {
        handleRequest(HttpMethod.Delete, "/hall/invalid").apply {
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
        val srepo = FileSeatRepo()
    }
}