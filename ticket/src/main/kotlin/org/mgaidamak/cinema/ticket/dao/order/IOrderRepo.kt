package org.mgaidamak.cinema.ticket.dao.order

import org.mgaidamak.cinema.ticket.dao.Order
import org.mgaidamak.cinema.ticket.dao.Page

/**
 * DAO for Order list
 */
interface IOrderRepo {
    fun createOrder(order: Order): Order?
    fun getOrders(page: Page = Page(0, 10),
                  sort: List<String> = emptyList()): Collection<Order>
    fun getOrderById(id: Int): Order?
    fun deleteOrderById(id: Int): Order?
    fun clear()
    fun total(): Int
}