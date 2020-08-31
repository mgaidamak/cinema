package org.mgaidamak.dao.seat

import org.mgaidamak.dao.Seat
import org.mgaidamak.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbSeatRepo(private val url: String,
                 private val props: Properties = Properties()): ISeatRepo {

    override fun createSeat(seat: Seat): Seat? {
        val sql = "INSERT INTO seat (hall, x, y) VALUES (?, ?, ?) RETURNING id"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).apply {
                    setInt(1, seat.hall)
                    setInt(2, seat.x)
                    setInt(3, seat.y)
                }.executeQuery().let { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }.let {
                    Seat(it, seat.hall, seat.x, seat.y)
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
            DriverManager.getConnection(url, props).use { it ->
                it.prepareStatement(sql).apply {
                    setInt(1, hall)
                    setInt(2, page.limit)
                    setInt(3, page.offset)
                }.executeQuery().collect(emptyList())
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    private fun querySeat(id: Int, sql: String): Seat? {
        return try {
            DriverManager.getConnection(url, props).use { it ->
                it.prepareStatement(sql).apply {
                    setInt(1, id)
                }.executeQuery().let {
                    if (it.next()) Seat(id, it.getInt(1),
                        it.getInt(2), it.getInt(3)) else null
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

    override fun clear() {
        val sql = "DELETE FROM hall"
        try {
            DriverManager.getConnection(url, props).use {
                it.createStatement().executeUpdate(sql)
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun total(): Int {
        val sql = "SELECT COUNT(*) FROM seat"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.createStatement().executeQuery(sql).let { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }
            }
        } catch (e: Exception) {
            println(e)
            0
        }
    }
}