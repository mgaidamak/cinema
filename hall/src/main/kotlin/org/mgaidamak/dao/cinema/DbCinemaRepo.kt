package org.mgaidamak.dao.cinema

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.DbRepo
import org.mgaidamak.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbCinemaRepo(url: String,
                   props: Properties = Properties()): DbRepo(url, props), ICinemaRepo {

    override fun createCinema(cinema: Cinema): Cinema? {
        val sql = """INSERT INTO cinema (name, city, address, timezone)
                     VALUES (?, ?, ?, ?) RETURNING id, name, city, address, timezone"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setString(1, cinema.name)
                    ps.setString(2, cinema.city)
                    ps.setString(3, cinema.address)
                    ps.setString(4, cinema.timezone)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Cinema(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private tailrec fun ResultSet.collect(list: List<Cinema>): List<Cinema>
        = if (!next()) list else collect(list.plus(Cinema(this)))

    private fun getCinemas(page: Page, sort: List<String>): Collection<Cinema> {
        val sql = "SELECT id, name, city, address, timezone FROM cinema LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, page.limit)
                    ps.setInt(2, page.offset)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override fun getCinemas(city: String?, page: Page, sort: List<String>): Collection<Cinema> {
        if (city == null) return getCinemas(page, sort)
        val sql = """SELECT id, name, city, address, timezone FROM cinema 
                     WHERE city ILIKE '%' || ? || '%'
                     LIMIT ? OFFSET ?"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setString(1, city)
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

    private fun queryCinema(id: Int, sql: String): Cinema? {
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, id)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Cinema(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getCinemaById(id: Int): Cinema? {
        val sql = "SELECT id, name, city, address, timezone FROM cinema WHERE id = ?"
        return queryCinema(id, sql)
    }

    override fun deleteCinemaById(id: Int): Cinema? {
        val sql = "DELETE FROM cinema WHERE id = ? RETURNING id, name, city, address, timezone"
        return queryCinema(id, sql)
    }

    override fun clear() = clear("DELETE FROM cinema")

    override fun total() = total("SELECT COUNT(*) FROM cinema")
}