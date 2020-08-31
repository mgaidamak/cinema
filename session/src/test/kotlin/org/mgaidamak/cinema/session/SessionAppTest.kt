package org.mgaidamak.cinema.session

import io.ktor.http.*
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionAppTest {
    @Test
    fun `test root`() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertEquals("My Hall App", response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            film(repo)
        }, callback)
    }

    companion object {
        val repo = DbFilmRepo()
    }
}