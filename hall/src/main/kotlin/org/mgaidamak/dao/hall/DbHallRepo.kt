package org.mgaidamak.dao.hall

import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbHallRepo(private val url: String,
                 private val props: Properties = Properties()): IHallRepo {

    override fun createHall(hall: Hall): Hall? {
        val sql = "INSERT INTO hall (cinema, name) VALUES (?, ?) RETURNING id"
        try {
            return DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).apply {
                    setInt(1, hall.cinema)
                    setString(2, hall.name)
                }.executeQuery().let { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }.let {
                    Hall(it, hall.cinema, hall.name)
                }
            }
        } catch (e: Exception) {
            println(e)
            return null
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
            DriverManager.getConnection(url, props).use { it ->
                it.prepareStatement(sql).apply {
                    setInt(1, cinema)
                    setInt(2, page.limit)
                    setInt(3, page.offset)
                }.executeQuery().collect(emptyList())
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    private fun queryHall(id: Int, sql: String): Hall? {
        return try {
            DriverManager.getConnection(url, props).use { it ->
                it.prepareStatement(sql).apply {
                    setInt(1, id)
                }.executeQuery().let {
                    if (it.next()) Hall(id, it.getInt(1), it.getString(2)) else null
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
        val sql = "SELECT COUNT(*) FROM hall"
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