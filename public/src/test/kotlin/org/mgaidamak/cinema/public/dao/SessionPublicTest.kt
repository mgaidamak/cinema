package org.mgaidamak.cinema.public.dao

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Test Session part of Public repo with mocked http
 */
private class SessionRepo: MockPublicRepo() {
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
}

class SessionPublicTest {

    private val repo = SessionRepo()

    @Test
    fun `get sessions`() {
        val halls = IntArray(2) { it + 10 }
        val sessions = runBlocking { repo.getSessions(halls) }
        val expected = listOf(
            AdminSession(1, 1, 11, "2020-09-10T00:00:00Z", 100),
            AdminSession(2, 2, 11, "2020-09-10T03:00:00Z", 200),
        )
        assertEquals(HttpStatusCode.OK, sessions.code)
        assertEquals(expected, sessions.data)
    }

    @Test
    fun `get film`() {
        val film = runBlocking { repo.getFilm(1) }
        val expected = AdminFilm(1, "Some like it hot")
        assertEquals(HttpStatusCode.OK, film.code)
        assertEquals(expected, film.data)
    }

    @Test
    fun `get session`() {
        val session = runBlocking { repo.getSession(2) }
        val expected = AdminSession(2, 2, 11, "2020-09-10T03:00:00Z", 200)
        assertEquals(HttpStatusCode.OK, session.code)
        assertEquals(expected, session.data)
    }
}