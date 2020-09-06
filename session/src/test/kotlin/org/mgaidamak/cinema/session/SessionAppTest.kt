package org.mgaidamak.cinema.session

import io.ktor.http.*
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.mgaidamak.cinema.session.dao.Film
import org.mgaidamak.cinema.session.dao.Session
import org.mgaidamak.cinema.session.dao.film.DbFilmRepo
import org.mgaidamak.cinema.session.dao.film.DbFilmRepoTest
import org.mgaidamak.cinema.session.dao.session.DbSessionRepo
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SessionAppTest {
    @Test
    fun `test root`() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertEquals("My Session App", response.content)
        }
    }

    @BeforeTest
    fun `clean up`() {
        frepo.clear()
    }

    private fun insertFilm(name: String): Int {
        val film = Film(name = name)
        return assertNotNull(frepo.createFilm(film)).id
    }

    private fun insertSession(film: Int, hall: Int): Int {
        val session = Session(film = film, hall = hall,
        date = "2020-09-10T00:00:00Z",
        price = 100)
        return assertNotNull(srepo.createSession(session)).id
    }

    @Test
    fun `create film`() = testApp {
        handleRequest(HttpMethod.Post, "/film") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(buildJsonObject {
                put("name", "Some like it hot")
            }.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content)
        }
    }

    @Test
    fun `list film`() = testApp {
        val f1 = insertFilm("Some like it hot")
        val f2 = insertFilm("Great race")

        handleRequest(HttpMethod.Get, "/film").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonArray {
                add(buildJsonObject {
                    put("id", f1)
                    put("name", "Some like it hot")
                })
                add(buildJsonObject {
                    put("id", f2)
                    put("name", "Great race")
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `get film`() = testApp {
        val f1 = insertFilm("Some like it hot")
        handleRequest(HttpMethod.Get, "/film/$f1").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", f1)
                put("name", "Some like it hot")
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    @Test
    fun `delete film`() = testApp {
        val f1 = insertFilm("Some like it hot")
        handleRequest(HttpMethod.Delete, "/film/$f1").apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val expected = buildJsonObject {
                put("id", f1)
                put("name", "Some like it hot")
            }.toString()
            assertEquals(expected, response.content)
        }
        handleRequest(HttpMethod.Get, "/film/$f1").apply {
            assertEquals(HttpStatusCode.NotFound, response.status())
            assertEquals("Not found by $f1", response.content)
        }
    }

    @Test
    fun `create session`() = testApp {
        val f1 = insertFilm("Some like it hot")
        handleRequest(HttpMethod.Post, "/session") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(buildJsonObject {
                put("film", f1)
                put("hall", 1)
                put("date", "2020-09-10T00:00:00Z")
                put("price", 100)
            }.toString())
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            assertNotNull(response.content)
        }
    }

    @Test
    fun `list session`() = testApp {
        val f1 = insertFilm("Some like it hot")
        val s1 = insertSession(f1, 1)
        insertSession(f1, 2)

        handleRequest(HttpMethod.Get, "/session?hall=1").apply {
            assertEquals(200, response.status()?.value)
            val expected = buildJsonArray {
                add(buildJsonObject {
                    put("id", s1)
                    put("film", f1)
                    put("hall", 1)
                    put("date", "2020-09-10T00:00:00Z")
                    put("price", 100)
                })
            }.toString()
            assertEquals(expected, response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            common()
            film(frepo)
            session(srepo)
        }, callback)
    }

    companion object {
        val frepo = DbFilmRepo(DbFilmRepoTest.url)
        val srepo = DbSessionRepo(DbFilmRepoTest.url)
    }
}