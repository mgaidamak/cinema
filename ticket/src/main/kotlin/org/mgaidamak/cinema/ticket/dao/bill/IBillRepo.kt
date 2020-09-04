package org.mgaidamak.cinema.ticket.dao.bill

import org.mgaidamak.cinema.ticket.dao.Bill
import org.mgaidamak.cinema.ticket.dao.Page

/**
 * DAO for Bill list
 */
interface IBillRepo {
    fun createBill(bill: Bill): Bill?
    fun updateBill(bill: Bill): Bill?
    fun getBills(page: Page = Page(0, 10),
                  sort: List<String> = emptyList()): Collection<Bill>
    fun getBillById(id: Int): Bill?
    fun deleteBillById(id: Int): Bill?
    fun clear()
    fun total(): Int
}