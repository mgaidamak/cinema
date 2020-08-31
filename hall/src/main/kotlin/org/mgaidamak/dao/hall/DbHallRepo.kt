package org.mgaidamak.dao.hall

import org.mgaidamak.dao.DbRepo
import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbHallRepo(url: String,
                 props: Properties = Properties()): DbRepo(url, props), IHallRepo {

    override fun createHall(hall: Hall): Hall? {
        val sql = "INSERT INTO hall (cinema, name) VALUES (?, ?) RETURNING id"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, hall.cinema)
                    ps.setString(2, hall.name)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt(1) else null
                    }?.let {
                        Hall(it, hall.cinema, hall.name)
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    private tailrec fun ResultSet.collect(list: List<Hall>): List<Hall>
        = if (!next()) list else collect(list.plus(Hall(getInt(1), getInt(2),
        getString(3))))

    override fun getHalls(cinema: Int,
                          page: Page,
                          sort: List<String>): Collection<Hall> {
        val sql = "SELECT id, cinema, name FROM hall WHERE cinema=? LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, cinema)
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

    private fun queryHall(id: Int, sql: String): Hall? {
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, id)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Hall(id, rs.getInt(1), rs.getString(2)) else null
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getHallById(id: Int): Hall? {
        val sql = "SELECT cinema, name FROM hall WHERE id = ?"
        return queryHall(id, sql)
    }

    override fun deleteHallById(id: Int): Hall? {
        val sql = "DELETE FROM hall WHERE id = ? RETURNING cinema, name"
        return queryHall(id, sql)
    }

    override fun clear() = clear("DELETE FROM hall")

    override fun total() = total("SELECT COUNT(*) FROM hall")
}