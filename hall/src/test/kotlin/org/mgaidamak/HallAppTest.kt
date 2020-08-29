package org.mgaidamak

import io.ktor.http.*
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.mgaidamak.repo.FileHallRepo
import kotlin.test.Test
import kotlin.test.assertEquals

class HallAppTest {
    @Test
    fun testRoot() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertEquals("My Hall App", response.content)
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