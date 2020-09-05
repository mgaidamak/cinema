package org.mgaidamak.cinema.session

import io.ktor.http.*
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.mgaidamak.cinema.session.dao.film.DbFilmRepo
import org.mgaidamak.cinema.session.dao.film.DbFilmRepoTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionAppTest {
    @Test
    fun `test root`() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertEquals("My Session App", response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            film(repo)
        }, callback)
    }

    companion object {
        val repo = DbFilmRepo(DbFilmRepoTest.url)
    }
}