package org.mgaidamak.cinema.ticket.dao.seat

import org.mgaidamak.cinema.ticket.dao.Order
import org.mgaidamak.cinema.ticket.dao.Seat
import org.mgaidamak.cinema.ticket.dao.order.IOrderRepo
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
                             private val orepo: IOrderRepo) {
    @BeforeTest
    fun `clean up`() {
        repo.clear()
    }

    private fun createOrder(): Int {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        return assertNotNull(orepo.createOrder(order)).id
    }

    @Test
    open fun `create seat`() {
        val order = createOrder()
        assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 6, status = 0)))
        assertEquals(2, orepo.total())
    }

    @Test
    open fun `create seat duplicated`() {
        val order = createOrder()
        assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertEquals(1, orepo.total())
    }

    @Test
    fun `list seat`() {
        val order = createOrder()
        val first = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        val second = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 6, status = 0)))
        val expected = listOf(first, second)
        val found = repo.getSeats(2)
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
        assertEquals(2, orepo.total())
    }

    @Test
    fun `list seat wrong session`() {
        val order = createOrder()
        assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 6, status = 0)))
        val found = repo.getSeats(3)
        assertTrue { found.isEmpty() }
        assertEquals(2, orepo.total())
    }

    @Test
    fun `get seat by id`() {
        val order = createOrder()
        val first = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.getSeat(2, 5))
    }

    @Test
    fun `get seat by wrong seat`() {
        val order = createOrder()
        val first = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.getSeat(2, 6))
    }

    @Test
    fun `get seat by wrong session`() {
        val order = createOrder()
        val first = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.getSeat(3, 5))
    }

    @Test
    fun `delete seat`() {
        val order = createOrder()
        val first = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.deleteSeatById(first.id))
    }

    @Test
    fun `delete seat absent`() {
        val order = createOrder()
        val first = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertNull(repo.deleteSeatById(first.id - 1))
    }

    @Test
    fun `delete seat by order`() {
        val order = createOrder()
        val first = assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertEquals(first, repo.deleteSeatByOrder(order))
    }

    @Test
    fun `delete seat by order absent`() {
        val order = createOrder()
        assertNotNull(repo.createSeat(Seat(order = order, session = 2, seat = 5, status = 0)))
        assertNull(repo.deleteSeatByOrder(order - 1))
    }
}