package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page

import java.sql.DriverManager
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbHallRepo: IHallRepo {
    override fun createCinema(cinema: Cinema): Cinema {
        val sql = "INSERT INTO hall (name, city, address, timezone) VALUES (?, ?, ?, ?)"
        return DriverManager.getConnection(url, props).use { it ->
            it.prepareStatement(sql).apply {
                setString(0, cinema.name)
                setString(1, cinema.city)
                setString(2, cinema.address)
                setString(3, cinema.timezone)
            }.apply {
                execute()
            }.generatedKeys.let {
                if (it.next()) {
                    it.getLong(1)
                } else {
                    0
                }
            }.let {
                Cinema(it, cinema.name, cinema.city, cinema.address, cinema.timezone)
            }
        }
    }

    override fun getCinemas(page: Page, sort: List<String>): Collection<Cinema> {
        TODO("Not yet implemented")
    }

    override fun getCinemaById(id: Long): Cinema? {
        TODO("Not yet implemented")
    }

    override fun deleteCinemaById(id: Long): Cinema? {
        TODO("Not yet implemented")
    }

    override fun clear() {
    }

    override fun total(): Int {
        TODO("Not yet implemented")
    }

    companion object {
        val url = "jdbc:postgresql://127.0.0.1:5432/hall"
        val props = Properties().apply {
            put("user", "user")
            put("password", "password")
        }
    }
}