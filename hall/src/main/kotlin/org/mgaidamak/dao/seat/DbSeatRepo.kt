package org.mgaidamak.dao.seat

import org.mgaidamak.dao.DbRepo
import org.mgaidamak.dao.Seat
import org.mgaidamak.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbSeatRepo(url: String,
                 props: Properties = Properties()): DbRepo(url, props), ISeatRepo {

    override fun createSeat(seat: Seat): Seat? {
        val sql = "INSERT INTO seat (hall, x, y) VALUES (?, ?, ?) RETURNING id"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, seat.hall)
                    ps.setInt(2, seat.x)
                    ps.setInt(3, seat.y)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt(1) else null
                    }?.let {
                        Seat(it, seat.hall, seat.x, seat.y)
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    private tailrec fun ResultSet.collect(list: List<Seat>): List<Seat>
        = if (!next()) list else collect(list.plus(Seat(getInt(1),
        getInt(2), getInt(3), getInt(4))))

    override fun getSeats(hall: Int,
                          page: Page): Collection<Seat> {
        val sql = "SELECT id, hall, x, y FROM seat WHERE hall=? LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, hall)
                    ps.setInt(2, page.limit)
                    ps.setInt(3, page.offset)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    private fun querySeat(id: Int, sql: String): Seat? {
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, id)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Seat(id, rs.getInt(1),
                            rs.getInt(2), rs.getInt(3)) else null
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun deleteSeatById(id: Int): Seat? {
        val sql = "DELETE FROM seat WHERE id = ? RETURNING hall, x, y"
        return querySeat(id, sql)
    }

    override fun clear() = clear("DELETE FROM seat")

    override fun total() = total("SELECT COUNT(*) FROM seat")
}