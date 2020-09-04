package org.mgaidamak.cinema.ticket

import io.ktor.http.*
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.mgaidamak.cinema.ticket.dao.bill.DbBillRepo
import org.mgaidamak.cinema.ticket.dao.bill.DbBillRepoTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TicketAppTest {
    @Test
    fun `test root`() = testApp {
        handleRequest(HttpMethod.Get, "/").apply {
            assertEquals(200, response.status()?.value)
            assertEquals("My Ticket App", response.content)
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({
            bill(repo)
        }, callback)
    }

    companion object {
        val repo = DbBillRepo(DbBillRepoTest.url)
    }
}