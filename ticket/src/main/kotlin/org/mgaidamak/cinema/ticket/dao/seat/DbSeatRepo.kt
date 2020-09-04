package org.mgaidamak.cinema.ticket.dao.seat

import org.mgaidamak.cinema.ticket.dao.DbRepo
import org.mgaidamak.cinema.ticket.dao.Seat

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbSeatRepo(url: String,
                 props: Properties = Properties()): DbRepo<Seat>(url, props), ISeatRepo {

    override fun form(rs: ResultSet) = Seat(rs)

    override fun createSeat(seat: Seat): Seat? {
        val sql = """INSERT INTO seat (bill, session, seat, status)
                     VALUES (?, ?, ?, ?) RETURNING id, bill, session, seat, status"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, seat.bill)
                    ps.setInt(2, seat.session)
                    ps.setInt(3, seat.seat)
                    ps.setInt(4, seat.status)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) form(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun getSeats(session: Int): Collection<Seat> {
        val sql = "SELECT id, bill, session, seat, status FROM seat WHERE session = ?"
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
        val sql = "SELECT id, bill, session, seat, status FROM seat WHERE session = ? AND seat = ?"
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
        val sql = "DELETE FROM seat WHERE id = ? RETURNING id, bill, session, seat, status"
        return single(id, sql)
    }

    override fun deleteSeatByBill(bill: Int): Collection<Seat> {
        val sql = "DELETE FROM seat WHERE bill = ? RETURNING id, bill, session, seat, status"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, bill)
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