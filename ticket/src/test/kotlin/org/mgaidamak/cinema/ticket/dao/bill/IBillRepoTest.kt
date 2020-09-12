package org.mgaidamak.cinema.ticket.dao.bill

import org.mgaidamak.cinema.ticket.dao.Bill
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
abstract class IBillRepoTest(private val repo: IBillRepo) {
    @BeforeTest
    fun `clean up`() {
        repo.clear()
    }

    @Test
    open fun `create bill`() {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 100)
        val first = assertNotNull(repo.createBill(bill))
        val second = assertNotNull(repo.createBill(bill))
        assertEquals(2, repo.total())
        assertEquals(second.id, first.id + 1)
        val noidBill = first.copy(id = 0)
        assertEquals(bill, noidBill)
    }

    @Test
    fun `list bill`() {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createBill(bill))
        val second = assertNotNull(repo.createBill(bill))
        val expected = listOf(first, second)
        val found = assertNotNull(repo.getBills())
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list bill page`() {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        assertNotNull(repo.createBill(bill))
        val second = assertNotNull(repo.createBill(bill))
        val expected = listOf(second)
        val found = assertNotNull(repo.getBills(page = Page(1, 2)))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get bill by id`() {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createBill(bill))
        assertEquals(first, repo.getBillById(first.id))
    }

    @Test
    fun `get bill by id absent`() {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createBill(bill))
        assertNull(repo.getBillById(first.id - 1))
    }

    @Test
    fun `delete bill`() {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createBill(bill))
        assertEquals(first, repo.deleteBillById(first.id))
    }

    @Test
    fun `delete bill absent`() {
        val bill = Bill(customer = 1, session = 2, status = 0, total = 0)
        val first = assertNotNull(repo.createBill(bill))
        assertNull(repo.deleteBillById(first.id - 1))
    }
}