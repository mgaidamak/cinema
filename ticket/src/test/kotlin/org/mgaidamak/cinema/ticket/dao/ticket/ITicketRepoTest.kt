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

    private fun createBill(): Int {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        return assertNotNull(brepo.createBill(bill)).id
    }

    @Test
    open fun `create seat`() {
        val bill = createBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        assertEquals(2, repo.total())
    }

    @Test
    open fun `create seat duplicated`() {
        val bill = createBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(1, repo.total())
    }

    @Test
    fun `list seat`() {
        val bill = createBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.getTickets(2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
        assertEquals(2, repo.total())
    }

    @Test
    fun `list seat wrong session`() {
        val bill = createBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val found = assertNotNull(repo.getTickets(3))
        assertTrue { found.isEmpty() }
        assertEquals(2, repo.total())
    }

    @Test
    fun `get seat by id`() {
        val bill = createBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.getTicket(2, 5))
    }

    @Test
    fun `get seat by wrong seat`() {
        val bill = createBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.getTicket(2, 6))
    }

    @Test
    fun `get seat by wrong session`() {
        val bill = createBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.getTicket(3, 5))
    }

    @Test
    fun `delete seat`() {
        val bill = createBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.deleteTicketById(first.id))
    }

    @Test
    fun `delete seat absent`() {
        val bill = createBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.deleteTicketById(first.id - 1))
    }

    @Test
    fun `delete seat by bill`() {
        val bill = createBill()
        val first = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.deleteTicketByBill(bill))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `delete seat by bill absent`() {
        val bill = createBill()
        assertNotNull(repo.createTicket(Ticket(bill = bill, session = 2, seat = 5, status = 0)))
        val found = assertNotNull(repo.deleteTicketByBill(bill - 1))
        assertTrue { found.isEmpty() }
    }
}