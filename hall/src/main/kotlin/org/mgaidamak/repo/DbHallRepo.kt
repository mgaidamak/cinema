package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbHallRepo: IHallRepo {
    override fun createCinema(cinema: Cinema): Cinema {
        val sql = "INSERT INTO cinema (name, city, address, timezone) VALUES (?, ?, ?, ?) RETURNING id"
        return DriverManager.getConnection(url, props).use { connection ->
            connection.prepareStatement(sql).apply {
                setString(1, cinema.name)
                setString(2, cinema.city)
                setString(3, cinema.address)
                setString(4, cinema.timezone)
            }.executeQuery().let { rs ->
                if (rs.next()) rs.getLong(1) else 0
            }.let {
                Cinema(it, cinema.name, cinema.city, cinema.address, cinema.timezone)
            }
        }
    }

    private tailrec fun ResultSet.collect(list: List<Cinema>): List<Cinema>
        = if (!next()) list else collect(list.plus(Cinema(getLong(1), getString(2),
        getString(3), getString(4), getString(5))))

    override fun getCinemas(page: Page, sort: List<String>): Collection<Cinema> {
        val sql = "SELECT id, name, city, address, timezone FROM cinema LIMIT ? OFFSET ?"
        return DriverManager.getConnection(url, props).use { it ->
            it.prepareStatement(sql).apply {
                setLong(1, page.limit)
                setLong(2, page.offset)
            }.executeQuery().collect(emptyList())
        }
    }

    private fun queryCinema(id: Long, sql: String): Cinema? {
        return DriverManager.getConnection(url, props).use { it ->
            it.prepareStatement(sql).apply {
                setLong(1, id)
            }.executeQuery().let {
                if (it.next()) Cinema(id, it.getString(1), it.getString(2),
                    it.getString(3), it.getString(4)) else null
            }
        }
    }

    override fun getCinemaById(id: Long): Cinema? {
        val sql = "SELECT name, city, address, timezone FROM cinema WHERE id = ?"
        return queryCinema(id, sql)
    }

    override fun deleteCinemaById(id: Long): Cinema? {
        val sql = "DELETE FROM cinema WHERE id = ? RETURNING name, city, address, timezone"
        return queryCinema(id, sql)
    }

    override fun clear() {
        val sql = "DELETE FROM cinema"
        DriverManager.getConnection(url, props).use {
            it.createStatement().executeUpdate(sql)
        }
    }

    override fun total(): Int {
        val sql = "SELECT COUNT(*) FROM cinema"
        return DriverManager.getConnection(url, props).use { connection ->
            connection.createStatement().executeQuery(sql).let { rs ->
                if (rs.next()) rs.getInt(1) else 0
            }
        }
    }

    companion object {
        // TODO use application.conf
        val url = "jdbc:postgresql://127.0.0.1:5432/hall"
        val props = Properties().apply {
            put("user", "cinema")
            put("password", "cinemapass")
        }
    }
}