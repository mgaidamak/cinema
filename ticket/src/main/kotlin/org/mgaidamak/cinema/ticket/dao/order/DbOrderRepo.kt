package org.mgaidamak.cinema.ticket.dao.order

import org.mgaidamak.cinema.ticket.dao.DbRepo
import org.mgaidamak.cinema.ticket.dao.Order
import org.mgaidamak.cinema.ticket.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbOrderRepo(url: String,
                  props: Properties = Properties()): DbRepo<Order>(url, props), IOrderRepo {

    override fun form(rs: ResultSet) = Order(rs)

    override fun createOrder(order: Order): Order? {
        val sql = """INSERT INTO order (customer, session, status, total)
                     VALUES (?, ?, ?, ?) RETURNING id, customer, session, status, total"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, order.customer)
                    ps.setInt(2, order.session)
                    ps.setInt(3, order.status)
                    ps.setInt(4, order.total)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Order(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun updateOrder(order: Order): Order? {
        TODO("Not yet implemented")
    }

    override fun getOrders(page: Page, sort: List<String>): Collection<Order> {
        val sql = "SELECT id, customer, session, status, total FROM order LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, page.limit)
                    ps.setInt(2, page.offset)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getOrderById(id: Int): Order? {
        val sql = "SELECT id, customer, session, status, total FROM order WHERE id = ?"
        return single(id, sql)
    }

    override fun deleteOrderById(id: Int): Order? {
        val sql = "DELETE FROM order WHERE id = ? RETURNING id, customer, session, status, total"
        return single(id, sql)
    }

    override fun clear() = clear("DELETE FROM order")

    override fun total() = total("SELECT COUNT(*) FROM order")
}