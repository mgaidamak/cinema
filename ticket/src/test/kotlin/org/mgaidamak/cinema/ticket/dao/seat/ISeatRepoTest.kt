package org.mgaidamak.cinema.ticket.dao.seat

import org.mgaidamak.cinema.ticket.dao.Bill
import org.mgaidamak.cinema.ticket.dao.Seat
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
abstract class ISeatRepoTest(private val repo: ISeatRepo,
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
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 6, status = 0)))
        assertEquals(2, repo.total())
    }

    @Test
    open fun `create seat duplicated`() {
        val bill = createBill()
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(1, repo.total())
    }

    @Test
    fun `list seat`() {
        val bill = createBill()
        val first = assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.getSeats(2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
        assertEquals(2, repo.total())
    }

    @Test
    fun `list seat wrong session`() {
        val bill = createBill()
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 6, status = 0)))
        val found = assertNotNull(repo.getSeats(3))
        assertTrue { found.isEmpty() }
        assertEquals(2, repo.total())
    }

    @Test
    fun `get seat by id`() {
        val bill = createBill()
        val first = assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.getSeat(2, 5))
    }

    @Test
    fun `get seat by wrong seat`() {
        val bill = createBill()
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.getSeat(2, 6))
    }

    @Test
    fun `get seat by wrong session`() {
        val bill = createBill()
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.getSeat(3, 5))
    }

    @Test
    fun `delete seat`() {
        val bill = createBill()
        val first = assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.deleteSeatById(first.id))
    }

    @Test
    fun `delete seat absent`() {
        val bill = createBill()
        val first = assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        assertNull(repo.deleteSeatById(first.id - 1))
    }

    @Test
    fun `delete seat by bill`() {
        val bill = createBill()
        val first = assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.deleteSeatByBill(bill))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `delete seat by bill absent`() {
        val bill = createBill()
        assertNotNull(repo.createSeat(Seat(bill = bill, session = 2, seat = 5, status = 0)))
        val found = assertNotNull(repo.deleteSeatByBill(bill - 1))
        assertTrue { found.isEmpty() }
    }
}