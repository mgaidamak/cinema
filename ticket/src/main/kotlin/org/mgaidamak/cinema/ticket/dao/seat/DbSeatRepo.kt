package org.mgaidamak.cinema.ticket.dao.seat

import org.mgaidamak.cinema.ticket.dao.DbRepo
import org.mgaidamak.cinema.ticket.dao.Seat
import org.mgaidamak.cinema.ticket.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbSeatRepo(url: String,
                 props: Properties = Properties()): DbRepo<Seat>(url, props), ISeatRepo {

    override fun form(rs: ResultSet) = Seat(rs)

    override fun createSeat(order: Seat): Seat? {
        val sql = """INSERT INTO seat (order, session, seat, status)
                     VALUES (?, ?, ?, ?) RETURNING id, order, session, seat, status"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, order.order)
                    ps.setInt(2, order.session)
                    ps.setInt(3, order.seat)
                    ps.setInt(4, order.status)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Seat(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun getSeats(session: Int): Collection<Seat> {
        val sql = "SELECT id, order, session, seat, status FROM seat WHERE session = ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, session)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getSeat(session: Int, seat: Int): Seat? {
        val sql = "SELECT id, order, session, seat, status FROM seat WHERE session = ? AND seat = ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, session)
                    ps.setInt(2, seat)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) form(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteSeatById(id: Int): Seat? {
        val sql = "DELETE FROM seat WHERE id = ? RETURNING id, order, session, seat, status"
        return single(id, sql)
    }

    override fun deleteSeatByOrder(order: Int): Collection<Seat> {
        val sql = "DELETE FROM seat WHERE order = ? RETURNING id, order, session, seat, status"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, order)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun clear() = clear("DELETE FROM seat")

    override fun total() = total("SELECT COUNT(*) FROM seat")
}