package org.mgaidamak

import io.ktor.http.*
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.mgaidamak.repo.FileHallRepo
import kotlin.test.AfterTest
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
        repo.next.set(0)
        repo.map.clear()
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
                put("id", 1)
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
        repo.map[2] = Cinema(2, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

        handleRequest(HttpMethod.Get, "/cinema/2").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", 2)
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
        repo.map[2] = Cinema(3, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

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
        repo.map[4] = Cinema(4, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")
        repo.map[5] = Cinema(5, "Cosmos", "Moscow", "Petrovka 38", "Europe/Moscow")

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
        repo.map[6] = Cinema(6, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

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
        repo.map[6] = Cinema(6, "Pobeda", "Novosibirsk", "Lenina 1", "Asia/Novosibirsk")

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
            hallWithDependencies(repo)
        }, callback)
    }

    companion object {
        val repo = FileHallRepo()
    }
}