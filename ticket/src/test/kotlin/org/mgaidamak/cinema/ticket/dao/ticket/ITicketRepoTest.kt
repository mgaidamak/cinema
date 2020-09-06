package org.mgaidamak.cinema.ticket.dao.ticket

import org.mgaidamak.cinema.ticket.dao.Bill
import org.mgaidamak.cinema.ticket.dao.Ticket
import org.mgaidamak.cinema.ticket.dao.bill.IBillRepo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class ITicketRepoTest(private val repo: ITicketRepo,
                               private val brepo: IBillRepo) {
    @BeforeTest
    fun `clean up`() {
        brepo.clear()
    }

    private fun insertBill(): Int {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        return assertNotNull(brepo.createBill(bill)).id
    }

    @Test
    open fun `create ticket`() {
        val bill = insertBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        assertEquals(2, repo.total())
    }

    @Test
    open fun `create ticket duplicated`() {
        val bill = insertBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(1, repo.total())
    }

    @Test
    fun `list ticket at session`() {
        val bill = insertBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.getTickets(2, null))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
        assertEquals(2, repo.total())
    }

    @Test
    fun `list ticket at wrong session`() {
        val bill = insertBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val found = assertNotNull(repo.getTickets(3, null))
        assertTrue { found.isEmpty() }
        assertEquals(2, repo.total())
    }

    @Test
    fun `list ticket at bill`() {
        val bill = insertBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.getTickets(null, bill))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
        assertEquals(2, repo.total())
    }

    @Test
    fun `list ticket at wrong bill`() {
        val bill = insertBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val found = assertNotNull(repo.getTickets(null, bill + 1))
        assertTrue { found.isEmpty() }
        assertEquals(2, repo.total())
    }

    @Test
    fun `list ticket at both defined`() {
        val bill = insertBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 3, seat = 6, status = 0)))
        val expected = listOf(first)
        val found = assertNotNull(repo.getTickets(2, bill))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
        assertEquals(2, repo.total())
    }

    @Test
    fun `get ticket by id`() {
        val bill = insertBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.getTicket(2, 5))
    }

    @Test
    fun `get ticket by wrong seat`() {
        val bill = insertBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.getTicket(2, 6))
    }

    @Test
    fun `get ticket by wrong session`() {
        val bill = insertBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.getTicket(3, 5))
    }

    @Test
    fun `delete ticket`() {
        val bill = insertBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.deleteTicketById(first.id))
    }

    @Test
    fun `delete ticket absent`() {
        val bill = insertBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.deleteTicketById(first.id - 1))
    }

    @Test
    fun `delete ticket by bill`() {
        val bill = insertBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.deleteTicketByBill(bill))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `delete ticket by bill absent`() {
        val bill = insertBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        val found = assertNotNull(repo.deleteTicketByBill(bill - 1))
        assertTrue { found.isEmpty() }
    }
}