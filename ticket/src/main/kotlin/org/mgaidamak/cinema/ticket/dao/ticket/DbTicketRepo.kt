package org.mgaidamak.cinema.ticket.dao.ticket

import org.mgaidamak.cinema.ticket.dao.DbRepo
import org.mgaidamak.cinema.ticket.dao.Ticket

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbTicketRepo(url: String,
                   props: Properties = Properties()): DbRepo<Ticket>(url, props), ITicketRepo {

    override fun form(rs: ResultSet) = Ticket(rs)

    override fun createTicket(ticket: Ticket): Ticket? {
        val sql = """INSERT INTO seat (bill, session, seat, status)
                     VALUES (?, ?, ?, ?) RETURNING id, bill, session, seat, status"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, ticket.bill)
                    ps.setInt(2, ticket.session)
                    ps.setInt(3, ticket.seat)
                    ps.setInt(4, ticket.status)
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

    override fun getTickets(session: Int): Collection<Ticket> {
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

    override fun getTicket(session: Int, seat: Int): Ticket? {
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

    override fun deleteTicketById(id: Int): Ticket? {
        val sql = "DELETE FROM seat WHERE id = ? RETURNING id, bill, session, seat, status"
        return single(id, sql)
    }

    override fun deleteTicketByBill(bill: Int): Collection<Ticket> {
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