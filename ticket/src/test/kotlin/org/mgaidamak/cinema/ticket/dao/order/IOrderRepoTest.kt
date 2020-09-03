package org.mgaidamak.cinema.ticket.dao.order

import org.mgaidamak.cinema.ticket.dao.Order
import org.mgaidamak.cinema.ticket.dao.Page
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class IOrderRepoTest(private val repo: IOrderRepo) {
    @BeforeTest
    fun `clean up`() {
        repo.clear()
    }

    @Test
    open fun `create order`() {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        assertNotNull(repo.createOrder(order))
        assertNotNull(repo.createOrder(order))
        assertEquals(2, repo.total())
    }

    @Test
    fun `list order`() {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createOrder(order))
        val second = assertNotNull(repo.createOrder(order))
        val expected = listOf(first, second)
        val found = repo.getOrders()
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list order page`() {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        assertNotNull(repo.createOrder(order))
        val second = assertNotNull(repo.createOrder(order))
        val expected = listOf(second)
        val found = repo.getOrders(page = Page(1, 2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get order by id`() {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createOrder(order))
        assertEquals(first, repo.getOrderById(first.id))
    }

    @Test
    fun `get order by id absent`() {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createOrder(order))
        assertNull(repo.getOrderById(first.id - 1))
    }

    @Test
    fun `delete order`() {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createOrder(order))
        assertEquals(first, repo.deleteOrderById(first.id))
    }

    @Test
    fun `delete order absent`() {
        val order = Order(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createOrder(order))
        assertNull(repo.deleteOrderById(first.id - 1))
    }
}