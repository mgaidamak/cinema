package org.mgaidamak.cinema.public.dao

import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test Ticket part of Public repo with mocked http
 */
private class TicketRepo: MockPublicRepo() {
    override fun ticketGet(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/ticket?session=2" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 1)
                        put("bill", 4)
                        put("session", 2)
                        put("seat", 100)
                        put("status", 2)
                    })
                    add(buildJsonObject {
                        put("id", 2)
                        put("bill", 4)
                        put("session", 2)
                        put("seat", 101)
                        put("status", 2)
                    })
                }.toString() to HttpStatusCode.OK
            }
            "/bill/10" -> {
                buildJsonObject {
                    put("id", 10)
                    put("customer", 7)
                    put("session", 2)
                    put("status", 1)
                    put("total", 200)
                }.toString() to HttpStatusCode.OK
            }
            "/ticket?bill=10" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 11)
                        put("bill", 10)
                        put("session", 2)
                        put("seat", 102)
                        put("status", 2)
                    })
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }

    override fun ticketPost(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/bill" -> {
                buildJsonObject {
                    put("id", 10)
                    put("customer", 7)
                    put("session", 2)
                    put("status", 0)
                    put("total", 200)
                }.toString() to HttpStatusCode.OK
            }
            "/ticket" -> {
                buildJsonObject {
                    put("id", 11)
                    put("bill", 10)
                    put("session", 2)
                    put("seat", 102)
                    put("status", 2)
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }

    override fun ticketPatch(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/bill/10" -> {
                buildJsonObject {
                    put("id", 10)
                    put("customer", 7)
                    put("session", 2)
                    put("status", 1)
                    put("total", 200)
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }

    override fun ticketDelete(url: String): Pair<String, HttpStatusCode> {
        return when (url) {
            "/ticket?bill=10" -> {
                buildJsonArray {
                    add(buildJsonObject {
                        put("id", 11)
                        put("bill", 10)
                        put("session", 2)
                        put("seat", 102)
                        put("status", 2)
                    })
                }.toString() to HttpStatusCode.OK
            }
            else -> "" to HttpStatusCode.NotFound
        }
    }
}

class TicketPublicTest {

    private val repo = TicketRepo()

    @Test
    fun `get tickets`() {
        val tickets = runBlocking { repo.getTickets(2) }
        val expected = listOf(
            AdminTicket(1, 4, 2, 100, 2),
            AdminTicket(2, 4, 2, 101, 2)
        )
        assertEquals(HttpStatusCode.OK, tickets.code)
        assertEquals(expected, tickets.data)
    }

    @Test
    fun `create bill`() {
        val bill = runBlocking { repo.createBill(AdminBill(0, 7, 2, 0, 200)) }
        val expected = AdminBill(10, 7, 2, 0, 200)
        assertEquals(HttpStatusCode.OK, bill.code)
        assertEquals(expected, bill.data)
    }

    @Test
    fun `patch bill`() {
        val bill = runBlocking { repo.patchBill(AdminBill(10, 7, 2, 1, 200)) }
        val expected = AdminBill(10, 7, 2, 1, 200)
        assertEquals(HttpStatusCode.OK, bill.code)
        assertEquals(expected, bill.data)
    }

    @Test
    fun `create ticket`() {
        val bill = runBlocking { repo.createTicket(AdminTicket(0, 10, 2, 102, 2)) }
        val expected = AdminTicket(11, 10, 2, 102, 2)
        assertEquals(HttpStatusCode.OK, bill.code)
        assertEquals(expected, bill.data)
    }

    @Test
    fun `get bill`() {
        val bill = runBlocking { repo.getBill(10) }
        val expected = AdminBill(10, 7, 2, 1, 200)
        assertEquals(HttpStatusCode.OK, bill.code)
        assertEquals(expected, bill.data)
    }

    @Test
    fun `get bill tickets`() {
        val bill = runBlocking { repo.getBillTickets(10) }
        val expected = listOf(AdminTicket(11, 10, 2, 102, 2))
        assertEquals(HttpStatusCode.OK, bill.code)
        assertEquals(expected, bill.data)
    }

    @Test
    fun `delete bill tickets`() {
        val bill = runBlocking { repo.deleteBillTickets(10) }
        val expected = listOf(AdminTicket(11, 10, 2, 102, 2))
        assertEquals(HttpStatusCode.OK, bill.code)
        assertEquals(expected, bill.data)
    }
}